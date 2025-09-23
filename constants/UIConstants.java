package org.example.constants;

import java.awt.*;

/**
 * UI σταθερές - χρώματα, fonts, διαστάσεις κλπ
 */
public final class UIConstants {

    // WINDOW DIMENSIONS
    public static final int DEFAULT_WINDOW_WIDTH = 1200;
    public static final int DEFAULT_WINDOW_HEIGHT = 800;
    public static final int MIN_WINDOW_WIDTH = 800;
    public static final int MIN_WINDOW_HEIGHT = 600;

    // DIALOG DIMENSIONS
    public static final int DIALOG_DEFAULT_WIDTH = 500;
    public static final int DIALOG_DEFAULT_HEIGHT = 400;
    public static final int FORM_DIALOG_WIDTH = 600;
    public static final int FORM_DIALOG_HEIGHT = 500;

    // COMPONENT DIMENSIONS
    public static final int TEXT_FIELD_HEIGHT = 25;
    public static final int TEXT_FIELD_PREFERRED_WIDTH = 200;
    public static final int BUTTON_HEIGHT = 30;
    public static final int BUTTON_WIDTH = 100;

    // PADDING AND MARGINS
    public static final int SMALL_PADDING = 5;
    public static final int MEDIUM_PADDING = 10;
    public static final int LARGE_PADDING = 15;
    public static final int BORDER_PADDING = 10;

    // COLORS
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    public static final Color SECONDARY_COLOR = new Color(176, 196, 222);
    public static final Color SUCCESS_COLOR = new Color(60, 179, 113);
    public static final Color ERROR_COLOR = new Color(220, 20, 60);
    public static final Color WARNING_COLOR = new Color(255, 165, 0);
    public static final Color BACKGROUND_COLOR = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(192, 192, 192);

    // TABLE COLORS
    public static final Color TABLE_HEADER_COLOR = new Color(240, 240, 240);
    public static final Color TABLE_SELECTION_COLOR = new Color(184, 207, 229);
    public static final Color TABLE_GRID_COLOR = new Color(220, 220, 220);

    // FONTS
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 12);
    public static final Font LARGE_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    // BORDERS
    public static final int BORDER_THICKNESS = 1;
    public static final int ROUNDED_BORDER_RADIUS = 5;

    // COMPONENT GAPS
    public static final int HORIZONTAL_GAP = 5;
    public static final int VERTICAL_GAP = 5;
    public static final int FORM_VERTICAL_GAP = 10;
    public static final int FORM_HORIZONTAL_GAP = 10;

    // TABLE SETTINGS
    public static final int TABLE_ROW_HEIGHT = 20;
    public static final int TABLE_COLUMN_MARGIN = 3;
    public static final boolean TABLE_SHOW_GRID = true;

    // STATUS BAR
    public static final int STATUS_BAR_HEIGHT = 25;
    public static final Color STATUS_BAR_BACKGROUND = new Color(240, 240, 240);

    // MNEMONICS (για keyboard shortcuts)
    public static final char MNEMONIC_FILE = 'Α';
    public static final char MNEMONIC_VIEW = 'Π';
    public static final char MNEMONIC_TOOLS = 'Ε';
    public static final char MNEMONIC_HELP = 'Β';

    private UIConstants() {
        // Prevent instantiation
    }
}