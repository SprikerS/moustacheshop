package dev.sprikers.moustacheshop.components;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SidebarButton {
    private boolean active;
    private String icon;
    private String text;
    private Runnable action;

    public SidebarButton(String icon, String text, Runnable action) {
        this(false, icon, text, action);
    }
}
