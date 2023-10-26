package se.alipsa.symp;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.YearMonth;
import javax.swing.*;

public class HoverListCellRenderer extends DefaultListCellRenderer {

  private static final Color HOVER_COLOR = Color.LIGHT_GRAY;
  private int hoverIndex = -1;
  private MouseAdapter handler;

  static void register(JList<YearMonth> l) {
    HoverListCellRenderer renderer = new HoverListCellRenderer();
    l.setCellRenderer(renderer);
    l.addMouseListener(renderer.getHandler(l));
    l.addMouseMotionListener(renderer.getHandler(l));
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    if (!isSelected) {
      setBackground(index == hoverIndex ? HOVER_COLOR : list.getBackground());
    }
    return this;
  }

  public MouseAdapter getHandler(JList<YearMonth> list) {
    if (handler == null) {
      handler = new HoverMouseHandler(list);
    }
    return handler;
  }

  class HoverMouseHandler extends MouseAdapter {

    private final JList<YearMonth> list;

    public HoverMouseHandler(JList<YearMonth> list) {
      this.list = list;
    }

    @Override
    public void mouseExited(MouseEvent e) {
      setHoverIndex(-1);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      int index = list.locationToIndex(e.getPoint());
      setHoverIndex(list.getCellBounds(index, index).contains(e.getPoint())
          ? index : -1);
    }

    private void setHoverIndex(int index) {
      if (hoverIndex == index) return;
      hoverIndex = index;
      list.repaint();
    }
  }
}
