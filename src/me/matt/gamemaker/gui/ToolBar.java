package me.matt.gamemaker.gui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import me.matt.gamemaker.Main;
import me.matt.gamemaker.game.Game;

public class ToolBar extends JMenuBar {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Debugger debugger;

    private Main main;

    private final JMenu file;

    private final JMenu games;

    private final JMenuItem reload;

    private final JCheckBoxMenuItem fullScreen;

    private final JMenu help;

    private final JMenuItem information;
    private final JMenuItem about;
    private final JMenuItem debug;

    public ToolBar(final Main main) {
        this.main = main;
        file = new JMenu("File");
        games = new JMenu("Games");
        reload = new JMenuItem("Reload");
        fullScreen = new JCheckBoxMenuItem("Fullscreen");
        help = new JMenu("Help");
        about = new JMenuItem("About");
        debug = new JMenuItem("Debugging");
        information = new JMenuItem("Game Information");

        file.add(reload);
        help.add(about);
        help.add(debug);
        this.add(file);
        this.add(games);
        this.add(help);

        reload.addActionListener(e -> ToolBar.this.reload());
        about.addActionListener(e -> {
            boolean full = false;
            if (main.isFullScreenMode()) {
                main.changeFullScreenMode();
                full = true;
            }
            JOptionPane.showMessageDialog(main,
                    "Game maker V 1.0\n\nMade by: Matt Langlois");
            if (full) {
                main.changeFullScreenMode();
            }
        });
        fullScreen.addActionListener(evt -> main.changeFullScreenMode());
        debug.addActionListener(arg0 -> {
            if (debugger != null && debugger.isVisible()) {
                return;
            }
            try {
                debugger = new Debugger();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void populateGames() {
        games.removeAll();
        if (main.getGamesHandler().getGames().isEmpty()) {
            final JMenuItem i = new JMenuItem("(none)");
            i.setEnabled(false);
            games.add(i);
            return;
        }
        for (final Game g : main.getGamesHandler().getGames()) {
            final JCheckBoxMenuItem i = new JCheckBoxMenuItem(
                    g.getName() != null ? g.getName() + " - " + g.getVersion()
                            : g.getClass().getName());
            if (main.getCurrentGame() != null) {
                if (g.getClass().getName()
                        .equals(main.getCurrentGame().getClass().getName())) {
                    i.setSelected(true);
                }
            }
            i.addActionListener(e -> {
                if (i.isSelected()) {
                    main.disableCurrentGame();
                    main.startGame(g);
                    ToolBar.this.populateGames();
                } else if (!i.isSelected()) {
                    main.disableCurrentGame();
                    ToolBar.this.populateGames();
                }
            });
            games.add(i);
        }
    }

    public void reload() {
        if (debugger != null && debugger.isVisible()) {
            debugger.clear();
        }
        if (main.getCurrentGame() != null) {
            main.getCurrentGame().onDisable();
        }
        main.getGamesHandler().reload();
        main.reset();
        this.populateGames();
    }

    public void setFullscreenAllowed(final boolean allowed) {
        if (!allowed) {
            file.remove(fullScreen);
            if (main.isFullScreenMode()) {
                main.changeFullScreenMode();
                fullScreen.setSelected(false);
            }
        } else if (allowed) {
            for (final Component e : file.getMenuComponents()) {
                if (e.equals(fullScreen)) {
                    return;
                }
            }
            file.add(fullScreen);
        }
    }

    public void setGameInformation(final String info) {
        if (info != null) {
            help.add(information);
            for (final ActionListener al : information.getActionListeners()) {
                information.removeActionListener(al);
            }
            information.addActionListener(e -> {
                boolean full = false;
                if (main.isFullScreenMode()) {
                    main.changeFullScreenMode();
                    full = true;
                }
                JOptionPane.showMessageDialog(main, info);
                if (full) {
                    main.changeFullScreenMode();
                }
            });
        } else {
            help.remove(information);
        }
    }

}
