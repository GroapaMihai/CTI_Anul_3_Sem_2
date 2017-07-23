package Components;

import java.sql.Date;
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class CustomJTable extends JTable {
	private static final long serialVersionUID = 1L;
	private DefaultTableModel tableModel = (DefaultTableModel) getModel();
	private TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<> (tableModel);
	private IntComparator intComp = new IntComparator();
	private FloatComparator floatComp = new FloatComparator();
	private DateComparator dateComp = new DateComparator();

	public CustomJTable(TableModel tableModel) {
		super(tableModel);
		setRowSorter(sorter);
	}

	public void hideColumn(int columnIndex) {
		TableColumn column = getColumnModel().getColumn(columnIndex);

		column.setMinWidth(0);
		column.setMaxWidth(0);
		column.setWidth(0);
	}
	
	public void removeAllRows() {
		for (int i = tableModel.getRowCount() - 1; i >= 0; i--)
			tableModel.removeRow(i);
	}

	public void filter(String query) {
		sorter.setRowFilter(RowFilter.regexFilter(query));
	}

	private class IntComparator implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            Integer int1 = Integer.parseInt(o1.toString());
            Integer int2 = Integer.parseInt(o2.toString());
            return int1.compareTo(int2);
        }
    }

	private class FloatComparator implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            Float float1 = Float.parseFloat(o1.toString());
    		Float float2 = Float.parseFloat(o2.toString());
            return float1.compareTo(float2);
        }
    }

	private class DateComparator implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
        	Date d1 = (Date) o1;
        	Date d2 = (Date) o2;
            return d1.compareTo(d2);
        }
    }

	public void setIntComparatorAt(int columnIndex) {
		sorter.setComparator(columnIndex, intComp);
	}

	public void setFloatComparatorAt(int columnIndex) {
		sorter.setComparator(columnIndex, floatComp);
	}

	public void setDateComparatorAt(int columnIndex) {
		sorter.setComparator(columnIndex, dateComp);
	}
}
