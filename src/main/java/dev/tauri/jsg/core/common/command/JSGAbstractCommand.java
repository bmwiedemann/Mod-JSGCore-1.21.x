package dev.tauri.jsg.core.common.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public abstract class JSGAbstractCommand {

    public final JSGCommand baseCommand;

    public JSGAbstractCommand(JSGCommand baseCommand) {
        this.baseCommand = baseCommand;
        baseCommand.registerSubCommand(this);
    }

    public final ArgumentBuilder<CommandSourceStack, ?> register(String command) {
        return registerArguments(Commands.literal(command).requires(cs -> cs.hasPermission(getPermissions())));
    }

    public List<String> getAliases(){
        return List.of();
    }

    public abstract String getName();

    public abstract String getGeneralUsage();

    public abstract String getDescription();

    public int getPermissions() {
        return 2;
    }

    public abstract ArgumentBuilder<CommandSourceStack, ?> registerArguments(ArgumentBuilder<CommandSourceStack, ?> command);
}
