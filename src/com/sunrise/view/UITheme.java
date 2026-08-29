package com.sunrise.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * The colours, the fonts and the small helper methods that give the same look
 * to every window of the clinic system.
 */
public class UITheme {

	public static final Color PRIMARY = new Color(27, 94, 158);
	public static final Color PRIMARY_DARK = new Color(18, 68, 116);
	public static final Color ACCENT = new Color(0, 150, 136);
	public static final Color DANGER = new Color(198, 62, 62);
	public static final Color BACKGROUND = new Color(243, 246, 250);
	public static final Color CARD = Color.WHITE;
	public static final Color TEXT = new Color(35, 45, 55);
	public static final Color TEXT_LIGHT = new Color(110, 120, 130);
	public static final Color BORDER = new Color(214, 222, 230);

	public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
	public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
	public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	public static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 13);

	/** Main button of a window, for example Login or Save. */
	public static JButton primaryButton(String text) {
		return button(text, PRIMARY, Color.WHITE);
	}

	/** Second button, for example Clear or Close. */
	public static JButton secondaryButton(String text) {
		JButton button = button(text, Color.WHITE, PRIMARY);
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PRIMARY, 1),
				BorderFactory.createEmptyBorder(9, 17, 9, 17)));
		return button;
	}

	public static JButton dangerButton(String text) {
		return button(text, DANGER, Color.WHITE);
	}

	private static JButton button(String text, Color background, Color foreground) {
		JButton button = new JButton(text);

		// the windows look and feel paints its own grey background and hides
		// our colours, so the simple button drawing is used here
		button.setUI(new javax.swing.plaf.basic.BasicButtonUI());

		button.setFont(NORMAL_FONT);
		button.setBackground(background);
		button.setForeground(foreground);
		button.setFocusPainted(false);
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return button;
	}

	public static JLabel title(String text) {
		JLabel label = new JLabel(text);
		label.setFont(TITLE_FONT);
		label.setForeground(PRIMARY_DARK);
		return label;
	}

	public static JLabel heading(String text) {
		JLabel label = new JLabel(text);
		label.setFont(HEADING_FONT);
		label.setForeground(TEXT);
		return label;
	}

	public static JLabel label(String text) {
		JLabel label = new JLabel(text);
		label.setFont(NORMAL_FONT);
		label.setForeground(TEXT);
		return label;
	}

	public static JLabel hint(String text) {
		JLabel label = new JLabel(text);
		label.setFont(SMALL_FONT);
		label.setForeground(TEXT_LIGHT);
		return label;
	}

	public static JTextField textField(int columns) {
		JTextField field = new JTextField(columns);
		field.setFont(NORMAL_FONT);
		field.setBorder(fieldBorder());
		return field;
	}

	public static Border fieldBorder() {
		return BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(7, 9, 7, 9));
	}

	/** White box with a thin border, used for the forms. */
	public static JPanel card() {
		JPanel panel = new JPanel();
		panel.setBackground(CARD);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				BorderFactory.createEmptyBorder(18, 20, 18, 20)));
		return panel;
	}

	/** Blue strip at the top of every window. */
	public static JPanel header(String titleText, String subtitleText) {
		JPanel panel = new JPanel();
		panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
		panel.setBackground(PRIMARY);
		panel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

		JLabel title = new JLabel(titleText);
		title.setFont(TITLE_FONT);
		title.setForeground(Color.WHITE);
		panel.add(title);

		if (subtitleText != null) {
			JLabel subtitle = new JLabel(subtitleText);
			subtitle.setFont(SMALL_FONT);
			subtitle.setForeground(new Color(215, 230, 245));
			panel.add(subtitle);
		}
		return panel;
	}

	public static void styleTable(JTable table) {
		table.setFont(NORMAL_FONT);
		table.setRowHeight(26);
		table.setGridColor(BORDER);
		table.setSelectionBackground(new Color(214, 231, 247));
		table.setSelectionForeground(TEXT);
		table.getTableHeader().setFont(HEADING_FONT.deriveFont(13f));
		table.getTableHeader().setBackground(new Color(233, 239, 245));
		table.getTableHeader().setForeground(TEXT);
	}

	public static void pad(JComponent component, int top, int left, int bottom, int right) {
		component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
	}

	/** Puts a window in the middle of the screen. */
	public static void center(Window window) {
		window.setLocationRelativeTo(null);
	}

	/** Small icon in the title bar, drawn from the clinic colours. */
	public static java.awt.Image appIcon() {
		java.awt.image.BufferedImage image =
				new java.awt.image.BufferedImage(32, 32, java.awt.image.BufferedImage.TYPE_INT_ARGB);

		java.awt.Graphics2D g = image.createGraphics();
		g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
				java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(PRIMARY);
		g.fillRoundRect(0, 0, 32, 32, 8, 8);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Segoe UI", Font.BOLD, 18));
		g.drawString("S", 11, 23);
		g.dispose();

		Toolkit.getDefaultToolkit();
		return image;
	}
}
