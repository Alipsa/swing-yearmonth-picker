package se.alipsa.symp;

import static java.awt.Image.SCALE_SMOOTH;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public class YearMonthPicker extends JPanel {

    private Locale locale;
    private YearMonth startYearMonth;
    private YearMonth endYearMonth;
    private YearMonth initial;
    private JLabel inputField;
    private String monthPattern;
    private Popup popup;
    private final DateTimeFormatter yearMonthFormatter;
    private YearMonth selectedItem;
    private JButton pickerButton;

    JList<YearMonth> listView = new JList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame( "Year month picker" );
        JPanel panel = new JPanel();
        frame.add(panel);
        YearMonthPicker ymp = new YearMonthPicker();
        ymp.addListener(e -> System.out.println(ymp.getValue()));
        panel.add( new JLabel( "Default Picker: ") );
        panel.add(ymp);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public YearMonthPicker() {
        this(YearMonth.now());
    }

    public YearMonthPicker(YearMonth initial) {
        this(initial, Locale.getDefault());
    }

    public YearMonthPicker(YearMonth initial, Locale locale) {
        this(initial.minusYears(3), initial.plusYears(3), initial, locale, "MMMM");
    }

    public YearMonthPicker(YearMonth from, YearMonth to, YearMonth initial) {
        this(from, to, initial, "yyyy-MMM");
    }

    public YearMonthPicker(YearMonth from, YearMonth to, YearMonth initial, String monthPattern) {
        this(from, to, initial, Locale.getDefault(), monthPattern);
    }

    public YearMonthPicker(YearMonth from, YearMonth to, YearMonth initial, Locale locale, String monthPattern) {
        this(from, to, initial, locale, monthPattern, "yyyy-MM");
    }

    public YearMonthPicker(YearMonth from, YearMonth to, YearMonth initial, Locale locale, String monthPattern, String yearMonthPattern) {
        setStart(from);
        setEnd(to);
        setInitial(initial);
        setLocale(locale);
        setMonthPattern(monthPattern);
        yearMonthFormatter = DateTimeFormatter.ofPattern(yearMonthPattern, locale);
        createLayout();
    }

    private void createLayout() {
        setLayout(new FlowLayout());

        setSelectedItem(initial);
        String ymVal = initial == null ? "" : yearMonthFormatter.format(initial);
        inputField = new JLabel(ymVal);

        add(inputField);

        ImageIcon icon = icon("/calendar.png", 20, 20);
        if (icon != null) {
            pickerButton = new JButton(icon);
        } else {
            pickerButton = new JButton();
        }
        inputField.setLabelFor(pickerButton);
        pickerButton.addActionListener(a -> showHideSelectBox());
        add(pickerButton);
    }

    private ImageIcon icon(String path, int width, int height) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            BufferedImage buttonIcon = ImageIO.read(is);
            return new ImageIcon(new ImageIcon(buttonIcon).getImage().getScaledInstance(width,height, SCALE_SMOOTH));
        } catch (IOException e) {
            return null;
        }
    }

    private void showHideSelectBox() {

        if (popup != null) {
            //This happens when the calendar button is clicked twice
            // in which case we don't want two popup windows to open
            // but rather close the existing one
            popup.hide();
            popup = null;
            return;
        }

        JPanel selectBox = new JPanel(new BorderLayout());
        JLabel yearLabel = new JLabel(String.valueOf(getSelectedItem().getYear()));

        DefaultListModel<YearMonth> listModel = new DefaultListModel<>();
        for (int i = 1; i <= 12; i++) {
            listModel.addElement(YearMonth.of(Integer.parseInt(yearLabel.getText()), i));
        }
        listView.setModel(listModel);
        HoverListCellRenderer.register(listView);

        JPanel top = new JPanel(new FlowLayout());
        selectBox.add(top, BorderLayout.NORTH);

        JButton yearBackButton = new JButton("<");
        yearBackButton.addActionListener(e -> {
            int yearNum = Year.parse(yearLabel.getText()).minusYears(1).getValue();
            if (yearNum < startYearMonth.getYear()) return;
            yearLabel.setText(String.valueOf(yearNum));
            listModel.removeAllElements();
            for (int i = 1; i <= 12; i++) {
                YearMonth val = YearMonth.of(yearNum, i);
                if (!val.isBefore(startYearMonth)) {
                    listModel.addElement(YearMonth.of(yearNum, i));
                }
            }
        });
        JButton yearForwardButton = new JButton(">");
        yearForwardButton.addActionListener(e -> {
            int yearNum = Year.parse(yearLabel.getText()).plusYears(1).getValue();
            if (yearNum > endYearMonth.getYear()) return;
            yearLabel.setText(String.valueOf(yearNum));
            listModel.removeAllElements();
            for (int i = 1; i <= 12; i++) {
                YearMonth val = YearMonth.of(yearNum, i);
                if (!val.isAfter(endYearMonth)) {
                    listModel.addElement(val);
                }
            }
        });
        top.add(yearBackButton);
        top.add(yearLabel);
        top.add(yearForwardButton);

        selectBox.add(listView, BorderLayout.CENTER);

        listView.addListSelectionListener( l -> {
            YearMonth newYearMonth = listView.getSelectedValue();
            //System.out.println("Selected " + newYearMonth);
            if (newYearMonth == null) return;
            inputField.setText(yearMonthFormatter.format(newYearMonth));
            setSelectedItem(newYearMonth);
            if (popup != null) {
                popup.hide();
                popup = null;
            }
        });

        int layoutX = (int) (this.getLocationOnScreen().getX() + pickerButton.getBounds().getX());
        int layoutY = (int)(this.getLocationOnScreen().getY() + pickerButton.getBounds().getY());
        PopupFactory factory = PopupFactory.getSharedInstance();
        popup = factory.getPopup(this, selectBox, layoutX, layoutY);
        popup.show();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public YearMonth getStart() {
        return startYearMonth;
    }

    private void setStart(YearMonth start) {
        this.startYearMonth = start;
    }

    public YearMonth getEnd() {
        return endYearMonth;
    }

    private void setEnd(YearMonth end) {
        this.endYearMonth = end;
    }

    public YearMonth getInitial() {
        return initial;
    }

    private void setInitial(YearMonth initial) {
        this.initial = initial;
    }

    public String getMonthPattern() {
        return monthPattern;
    }

    private void setMonthPattern(String monthPattern) {
        this.monthPattern = monthPattern;
    }

    public YearMonth getSelectedItem() {
        return selectedItem;
    }

    public YearMonth getValue() {
        return getSelectedItem();
    }

    public void setSelectedItem(YearMonth selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void addListener(ListSelectionListener listener) {
        listView.addListSelectionListener(listener);
    }
}
