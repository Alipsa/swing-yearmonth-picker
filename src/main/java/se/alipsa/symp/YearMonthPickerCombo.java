package se.alipsa.symp;

import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;

public class YearMonthPickerCombo extends JComboBox<YearMonth> {

    public static void main(String[] args) {
        JFrame frame = new JFrame( "Year month combo" );
        JPanel panel = new JPanel();
        frame.add(panel);
        YearMonthPickerCombo ympc = new YearMonthPickerCombo();
        panel.add( new JLabel( "Default Combo: ") );
        panel.add(ympc);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public YearMonthPickerCombo() {
        this(YearMonth.now());
    }

    public YearMonthPickerCombo(YearMonth initial) {
        this(initial.minusYears(3), initial.plusYears(3), initial);
    }

    public YearMonthPickerCombo(YearMonth from, YearMonth to, YearMonth initial) {
       this(from, to, initial, Locale.getDefault());
    }

    public YearMonthPickerCombo(YearMonth from, YearMonth to, YearMonth initial, Locale locale) {
        this(from, to, initial, locale, "yyyy-MM");
    }

    public YearMonthPickerCombo(YearMonth from, YearMonth to, YearMonth initial, Locale locale, String format) {
        YearMonth ym = from;
        while (! ym.isAfter(to)) {
            addItem(ym);
            ym = ym.plusMonths(1);
        }
        getModel().setSelectedItem(initial);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, locale);

        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
                return super.getListCellRendererComponent(
                    list, formatter.format((YearMonth)value), index, isSelected, cellHasFocus);
            }
        });
    }
}
