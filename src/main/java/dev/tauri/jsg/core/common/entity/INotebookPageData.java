package dev.tauri.jsg.core.common.entity;

public interface INotebookPageData {
    <D extends INotebookPageData> void update(D newData);
}
