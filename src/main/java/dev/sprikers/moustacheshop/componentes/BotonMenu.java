package dev.sprikers.moustacheshop.componentes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotonMenu {
    private boolean active;
    private String icon;
    private String text;
    private Runnable action;

    public BotonMenu(String icon, String text, Runnable action) {
        this(false, icon, text, action);
    }
}
