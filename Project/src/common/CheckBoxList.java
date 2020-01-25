package common;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;


/**
 * CheckBoxList为自定义复选框List，提供了获取选择选项集合的方法
 * @author always
 *
 */
public class CheckBoxList extends JList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7896533700038454365L;
	
	private List<CheckBoxItem> cbItemList;
	private ListModel<Object> cblistmodel;
	
	/**
	 * 默认构造函数
	 */
	public CheckBoxList(){
		
	}
	
	/**
	 * 含参构造函数
	 * @param ItemSet 选项名称及状态集合
	 */
	public CheckBoxList(Map<String,Boolean> ItemSet){
		Init(ItemSet);
	}
	
	/**
	 * 初始化
	 * @param Itemset
	 */
	public void Init(Map<String,Boolean> Itemset){
		this.cblistmodel = new DefaultListModel<Object>();
		this.cbItemList=new ArrayList<CheckBoxItem>();
		int cnt=0;
		for(Map.Entry<String,Boolean> entry : Itemset.entrySet()) {
			CheckBoxItem item=new CheckBoxItem(entry.getKey(),cnt++,entry.getValue());
			cbItemList.add(item);
			((DefaultListModel<Object>) cblistmodel).addElement(item);
		}
		this.setModel(this.cblistmodel);
		CheckBoxListCellRenderer renderer = new CheckBoxListCellRenderer();
		this.setCellRenderer(renderer);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		CheckBoxListener lst = new CheckBoxListener(this);
		this.addMouseListener(lst);
		this.addKeyListener(lst);
	}
	
	/**
	 * getSelectedItemsIndex()用于获取CheckBoxList选中选项的下标集合
	 * @return Integer[] 选中选项下标数组
	 */
	public Integer[] getSelectedItemsIndex(){
		ArrayList<Integer> selectedIndex=new ArrayList<Integer>();
		Integer res[];
		for(CheckBoxItem cbi:cbItemList) {
			if(cbi.isSelected()) {
				selectedIndex.add(cbi.getIndex());
			}
		}
		res=new Integer[selectedIndex.size()];
		selectedIndex.toArray(res);
		return res;
	}
	
	/**
	 * 获取全部选项的状态
	 * @return
	 */
	public Map<String,Boolean> getAllItem(){
		Map<String,Boolean> res=new HashMap<String,Boolean>();
		for(CheckBoxItem cbi:cbItemList) {
			res.put(cbi.getName(), cbi.isSelected());
		}
		return res;
	}
}

/**
 * CheckBoxListCellRenderer为自定义ListCell渲染器类继承自JCheckBox
 * @author always
 *
 */
class CheckBoxListCellRenderer extends JCheckBox implements ListCellRenderer<Object> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4201629995031003632L;
	
	protected static Border cb_noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	
	public CheckBoxListCellRenderer() {
		super();
		setOpaque(true);
		setBorder(cb_noFocusBorder);
	}
	
	public Component getListCellRendererComponent(JList<?> list, Object value,
	int index, boolean isSelected, boolean cellHasFocus) {
		//文本设置
		setText(value.toString());
		//前后背景设置
		setBackground(isSelected ? list.getSelectionBackground() : list
		.getBackground());
		setForeground(isSelected ? list.getSelectionForeground() : list
		.getForeground());
		//是否选择设置
		CheckBoxItem item = (CheckBoxItem) value;
		setSelected(item.isSelected());
		//字体及边框设置
		setFont(list.getFont());
		setBorder((cellHasFocus) ? UIManager
		.getBorder("List.focusCellHighlightBorder") : cb_noFocusBorder);
		return this;
	}
}

class CheckBoxListener implements MouseListener, KeyListener {
	protected JList<?> cb_list;
	
	public CheckBoxListener(CheckBoxList cbList) {
		cb_list=cbList;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == ' ')
			doCheck();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getX() < 20)
			doCheck();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	
	/**
	 * doCheck()为自定义选择操作方法
	 */
	protected void doCheck() {
		int index = cb_list.getSelectedIndex();
		if (index < 0)return;
		CheckBoxItem data = (CheckBoxItem) cb_list.getModel().getElementAt(index);
		data.invertSelected();
		cb_list.repaint();
	}
	
}

/**
 * CheckBoxItem为CheckBoxList选项类
 * @author always
 *
 */
class CheckBoxItem {
	protected String cbItem_name;
	protected int cbItem_index;
	protected boolean cbItem_selected;
	
	public CheckBoxItem(String name, int index,Boolean isSelected) {
		cbItem_name = name;
		cbItem_index = index;
		cbItem_selected = isSelected;
	}
	
	public String getName() {
		return cbItem_name;
	}
	
	public int getIndex() {
		return cbItem_index;
	}
	
	public void setSelected(boolean selected) {
		cbItem_selected = selected;
	}
	
	public void invertSelected() {
		cbItem_selected = !cbItem_selected;
	}
	
	public boolean isSelected() {
		return cbItem_selected;
	}
	
	public String toString() {
		return cbItem_name;
	}
}