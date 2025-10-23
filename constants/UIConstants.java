package org.pms.constants;

import java.awt.*;

/**
 * UI Constants
 */
public final class UIConstants {


    // BOOTSTRAP-LIKE COLOR PALETTE
    public static final Color PRIMARY_COLOR = new Color(25, 42, 86);        // Navy Blue
    public static final Color SECONDARY_COLOR = new Color(108, 117, 125);   // Gray
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);       // Green
    public static final Color DANGER_COLOR = new Color(220, 53, 69);        // Red
    public static final Color WARNING_COLOR = new Color(255, 193, 7);       // Yellow
    public static final Color INFO_COLOR = new Color(23, 162, 184);         // Cyan
    public static final Color LIGHT_COLOR = new Color(248, 249, 250);       // Light Gray
    public static final Color DARK_COLOR = new Color(52, 58, 64);           // Dark Gray

    // CUSTOM APP COLORS
    public static final Color ACCENT_COLOR = new Color(184, 134, 11);       // Dark Yellow
    public static final Color CHERRY_COLOR = new Color(161, 14, 27);        // Cherry Red

    // TEXT COLORS
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    public static final Color TEXT_WHITE = Color.WHITE;
    public static final Color TEXT_BLACK = Color.BLACK;
    public static final Color TEXT_MUTED = new Color(108, 117, 125);

    // BACKGROUND COLORS
    public static final Color BG_PRIMARY = PRIMARY_COLOR;
    public static final Color BG_SECONDARY = SECONDARY_COLOR;
    public static final Color BG_SUCCESS = SUCCESS_COLOR;
    public static final Color BG_DANGER = DANGER_COLOR;
    public static final Color BG_WARNING = WARNING_COLOR;
    public static final Color BG_INFO = INFO_COLOR;
    public static final Color BG_LIGHT = LIGHT_COLOR;
    public static final Color BG_DARK = DARK_COLOR;
    public static final Color BG_WHITE = Color.WHITE;
    public static final Color BG_ACCENT = ACCENT_COLOR;

    // BORDER COLORS
    public static final Color BORDER_PRIMARY = PRIMARY_COLOR;
    public static final Color BORDER_SECONDARY = new Color(222, 226, 230);
    public static final Color BORDER_SUCCESS = SUCCESS_COLOR;
    public static final Color BORDER_DANGER = DANGER_COLOR;
    public static final Color BORDER_WARNING = WARNING_COLOR;
    public static final Color BORDER_ACCENT = ACCENT_COLOR;
    public static final Color BORDER_LIGHT = new Color(222, 226, 230);
    public static final Color BORDER_DARK = DARK_COLOR;
    public static final Color BORDER_INFO = INFO_COLOR;

    // BUTTON THEMES
    public static final Color BUTTON_PRIMARY_BG = PRIMARY_COLOR;
    public static final Color BUTTON_PRIMARY_TEXT = TEXT_BLACK;
    public static final Color BUTTON_SECONDARY_BG = SECONDARY_COLOR;
    public static final Color BUTTON_SECONDARY_TEXT = TEXT_WHITE;
    public static final Color BUTTON_SUCCESS_BG = SUCCESS_COLOR;
    public static final Color BUTTON_SUCCESS_TEXT = TEXT_WHITE;
    public static final Color BUTTON_DANGER_BG = DANGER_COLOR;
    public static final Color BUTTON_DANGER_TEXT = TEXT_BLACK;
    public static final Color BUTTON_WARNING_BG = WARNING_COLOR;
    public static final Color BUTTON_WARNING_TEXT = TEXT_BLACK;
    public static final Color BUTTON_INFO_BG = INFO_COLOR;
    public static final Color BUTTON_INFO_TEXT = TEXT_WHITE;
    public static final Color BUTTON_ACCENT_BG = ACCENT_COLOR;
    public static final Color BUTTON_ACCENT_TEXT = TEXT_BLACK;
    public static final Color BUTTON_CHERRY_BG = CHERRY_COLOR;
    public static final Color BUTTON_CHERRY_TEXT = TEXT_BLACK;

    // FORM CONTROLS
    public static final Color INPUT_BG = BG_WHITE;
    public static final Color INPUT_TEXT = TEXT_PRIMARY;
    public static final Color INPUT_BORDER = BORDER_LIGHT;
    public static final Color INPUT_BORDER_FOCUS = BORDER_ACCENT;
    public static final Color COMBO_BG = BG_WHITE;
    public static final Color COMBO_TEXT = TEXT_PRIMARY;
    public static final Color CHECKBOX_BG = BG_PRIMARY;
    public static final Color CHECKBOX_TEXT = TEXT_WHITE;

    // TABLE COLORS
    public static final Color TABLE_HEADER_BG = LIGHT_COLOR;
    public static final Color TABLE_HEADER_TEXT = TEXT_PRIMARY;
    public static final Color TABLE_SELECTION_BG = new Color(184, 207, 229);
    public static final Color TABLE_GRID_COLOR = BORDER_LIGHT;
    public static final Color TABLE_BG = BG_WHITE;
    public static final Color TABLE_TEXT = TEXT_PRIMARY;

    // STATUS BAR
    public static final int STATUS_BAR_HEIGHT = 25;
    public static final Color STATUS_BAR_BG = BG_PRIMARY;
    public static final Color STATUS_BAR_TEXT = TEXT_WHITE;

    // FONTS
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 12);
    public static final Font LARGE_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 10);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 12);
    public static final Font SEARCH_TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 12);
    public static final Font STATUS_FONT = new Font("SansSerif", Font.BOLD, 12);

    // COMPONENT DIMENSIONS
    public static final int TEXT_FIELD_HEIGHT = 25;
    public static final int TEXT_FIELD_PREFERRED_WIDTH = 200;
    public static final int BUTTON_HEIGHT = 30;
    public static final int BUTTON_WIDTH = 100;

    // BUTTON SIZES
    public static final Dimension BUTTON_SM = new Dimension(80, 25);
    public static final Dimension BUTTON_MD = new Dimension(100, 30);
    public static final Dimension BUTTON_LG = new Dimension(120, 35);
    public static final Dimension BUTTON_XL = new Dimension(140, 40);

    // SPACING - Bootstrap-like
    public static final int SPACING_XS = 2;
    public static final int SPACING_SM = 5;
    public static final int SPACING_MD = 10;
    public static final int SPACING_LG = 15;
    public static final int SPACING_XL = 20;
    public static final int SPACING_XXL = 30;

    // PADDING AND MARGINS
    public static final int SMALL_PADDING = SPACING_SM;
    public static final int MEDIUM_PADDING = SPACING_MD;
    public static final int LARGE_PADDING = SPACING_LG;
    public static final int BORDER_PADDING = SPACING_MD;

    // LAYOUT SPACING
    public static final int HORIZONTAL_GAP = SPACING_SM;
    public static final int VERTICAL_GAP = SPACING_SM;
    public static final int FORM_VERTICAL_GAP = SPACING_MD;
    public static final int FORM_HORIZONTAL_GAP = SPACING_MD;
    public static final int COMPONENT_GAP = SPACING_MD;
    public static final int SECTION_GAP = SPACING_LG;
    public static final int PANEL_PADDING = SPACING_MD;

    // SEARCH PANEL SPECIFIC
    public static final int SEARCH_PANEL_TOP_PADDING = SPACING_MD;
    public static final int SEARCH_PANEL_SIDE_PADDING = SPACING_LG;
    public static final int SEARCH_PANEL_BOTTOM_PADDING = SPACING_LG;
    public static final Color SEARCH_PANEL_BG = BG_PRIMARY;
    public static final Color SEARCH_LABEL_COLOR = TEXT_WHITE;

    // BUTTON PANEL
    public static final int BUTTON_PANEL_PADDING = SPACING_MD;
    public static final int BUTTON_GROUP_GAP = SPACING_SM;
    public static final int BUTTON_SECTION_GAP = SPACING_XL;
    public static final Color BUTTON_PANEL_BG = LIGHT_COLOR;

    // LOGO
    public static final Dimension LOGO_SIZE = new Dimension(100, 50);

    // BORDERS
    public static final int BORDER_THICKNESS = 1;
    public static final int ROUNDED_BORDER_RADIUS = 5;
    public static final int BORDER_WIDTH_THICK = 2;



    private UIConstants() {
        // Prevent instantiation
    }
}