package dev.tauri.jsg.core.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.tauri.jsg.core.JSGCore;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JSGCommand {
    public final String commandName;
    protected final CommandHelp helpCommand;
    private final List<dev.tauri.jsg.core.common.command.JSGAbstractCommand> subCommands = new LinkedList<>();

    private boolean isRegistered = false;

    public JSGCommand(String commandName) {
        this.commandName = commandName;
        helpCommand = new CommandHelp(this);
    }

    public String getName() {
        return commandName;
    }

    public static final JSGCommand JSG_COMMAND_BASE = new JSGCommand("jsg");

    private void assertNotRegistered() {
        if (isRegistered)
            throw new UnsupportedOperationException("Tried to modify command when it's already registered!");
    }

    public void registerSubCommand(JSGAbstractCommand command) {
        assertNotRegistered();
        subCommands.add(command);
    }

    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        var base = LiteralArgumentBuilder.<CommandSourceStack>literal(commandName);
        for (var sub : subCommands) {
            for (var alias : sub.getAliases()) {
                base = base.then(sub.register(alias)).executes(ctx -> {
                    showHelp(ctx.getSource(), 1);
                    return 0;
                });
            }
            base = base.then(sub.register(sub.getName())).executes(ctx -> {
                showHelp(ctx.getSource(), 1);
                return 0;
            });
        }
        dispatcher.register(base);
        JSGCore.logger.info("Successfully registered Commands!");
        isRegistered = true;
    }

    public String getTitle() {
        return "Just Stargate Mod";
    }

    public boolean canUseCommand(CommandSourceStack sender, int requiredPerms) {
        if (requiredPerms <= 0) return true;
        return sender.hasPermission(requiredPerms);
    }

    public void showHelp(CommandSourceStack sender, int page) {
        sender.sendSystemMessage(Component.literal(ChatFormatting.STRIKETHROUGH + "      " + ChatFormatting.RESET + " " + ChatFormatting.AQUA + ChatFormatting.BOLD + getTitle() + " " + ChatFormatting.RESET + ChatFormatting.STRIKETHROUGH + "      "));

        ArrayList<JSGAbstractCommand> commands = new ArrayList<>();
        for (JSGAbstractCommand c : subCommands) {
            if (canUseCommand(sender, c.getPermissions())) {
                commands.add(c);
            }
        }

        int count = commands.size();
        final int perPage = 10;
        final int maxPage = (int) Math.ceil((double) count / perPage);
        page = Math.max(1, Math.min(maxPage, page));

        int start = perPage * (page - 1);
        int end = perPage * page;

        int i = 0;
        for (JSGAbstractCommand c : commands) {
            i++;
            if (i <= start) continue;
            if (i > end) break;
            sender.sendSystemMessage(getCommandTextComponentForHelp(c));
        }

        MutableComponent back = Component.literal("\u00a73\u00a7l\u00a7m<--\u00a7r").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getName() + " help " + (page - 1))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Previous page"))));
        MutableComponent next = Component.literal("\u00a73\u00a7l\u00a7m-->\u00a7r").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getName() + " help " + (page + 1))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Next page"))));
        MutableComponent arrows = Component.literal("       ");
        if (page - 1 > 0)
            arrows.append(back);
        else
            arrows.append(Component.literal("\u00a78\u00a7l\u00a7m<--\u00a7r"));
        arrows.append(Component.literal(" \u00a77(" + page + "/" + maxPage + ")\u00a7r "));
        if (page + 1 <= maxPage)
            arrows.append(next);
        else
            arrows.append(Component.literal("\u00a78\u00a7l\u00a7m-->\u00a7r"));
        sender.sendSystemMessage(arrows);
        sender.sendSystemMessage(Component.literal(ChatFormatting.STRIKETHROUGH + "                                "));
    }

    public MutableComponent getCommandTextComponentForHelp(JSGAbstractCommand cmd) {
        Style style = Style.EMPTY;
        HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + "/" + getName() + " " + cmd.getGeneralUsage() + "\n" + ChatFormatting.GRAY + cmd.getDescription()));
        style = style.withHoverEvent(he);
        ClickEvent ce = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + getName() + " " + cmd.getName());
        style = style.withClickEvent(ce);
        return Component.literal(ChatFormatting.DARK_AQUA + " /" + getName() + " " + cmd.getName()).setStyle(style);
    }

    public void sendNoPerms(CommandSourceStack sender) {
        sendErrorMess(sender, "You don't have permissions to do that!");
    }

    public void sendErrorMess(CommandSourceStack sender, String mess, Object... param) {
        sender.sendSystemMessage(Component.literal(" \u00a7c\u00a7l✘ \u00a77").append(Component.translatable(mess, param)));
    }

    public void sendSuccessMess(CommandSourceStack sender, String mess, Object... params) {
        sender.sendSystemMessage(Component.literal(" \u00a7a\u00a7l✔ \u00a77").append(Component.translatable(mess, params)));
    }

    public void sendInfoMess(CommandSourceStack sender, String mess, Object... param) {
        sender.sendSystemMessage(Component.literal(" \u00a73\u00a7l\u2502 \u00a77").append(Component.translatable(mess, param)));
    }

    public void sendUsageMess(CommandSourceStack sender, JSGAbstractCommand cmd) {
        sender.sendSystemMessage(Component.literal(" \u00a73\u00a7lUsage: \u00a77/" + getName() + " " + cmd.getGeneralUsage()));
    }

    public void sendRunningMess(CommandSourceStack sender, String mess, Object... param) {
        sender.sendSystemMessage(Component.literal(" \u00a76\u00a7l♺ \u00a77").append(Component.translatable(mess, param)));
    }


    public static class CommandHelp extends JSGAbstractCommand {
        public CommandHelp(JSGCommand baseCommand) {
            super(baseCommand);
        }

        @Override
        public int getPermissions() {
            return 0;
        }

        @Override
        public ArgumentBuilder<CommandSourceStack, ?> registerArguments(ArgumentBuilder<CommandSourceStack, ?> command) {
            return command.then(Commands
                    .argument("page", IntegerArgumentType.integer())
                    .executes(ctx -> {
                        baseCommand.showHelp(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "page"));
                        return 0;
                    })
            ).executes(ctx -> {
                baseCommand.showHelp(ctx.getSource(), 1);
                return 0;
            });
        }

        @Override
        public String getName() {
            return "help";
        }

        @Override
        public String getGeneralUsage() {
            return "help [page]";
        }

        @Override
        public String getDescription() {
            return "Shows this list";
        }
    }
}
