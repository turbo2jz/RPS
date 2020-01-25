package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import main.Router;

public class RouterPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * ��Ӧ·����
	 */
	private Router router;
	
	/**
	 * ֱ�������б�
	 */
	public JList<String> destiNetworkList;
	
	/**
	 * ·�ɱ����ʱ���ǩ
	 */
	public JLabel timeLabel;
	
	/**
	 * ·�ɱ�
	 */
	public JTable routingTable;
	
	/**
	 * ��Ϣ��ʾ
	 */
	public JTextArea infoShow;
	
	public RouterPanel(Router r) {
		router=r;
		router.setShowPanel(this);
		this.setLayout(new BorderLayout());
		Init();
	}
	
	private void Init() {
		//����
		this.setBorder(new TitledBorder(router.getRouterName()));
		//ֱ�������б�
		JPanel dNetworkListPanel=new JPanel();
		dNetworkListPanel.setPreferredSize(new Dimension(120,0));
		destiNetworkList=new JList<String>();
		dNetworkListPanel.setBorder(new TitledBorder("ֱ�������б�"));
		dNetworkListPanel.add(destiNetworkList);
		this.add(dNetworkListPanel,BorderLayout.WEST);
		//·�ɱ�
		JPanel routingListPanel=new JPanel();
		routingListPanel.setPreferredSize(new Dimension(0,300));
		routingTable = new JTable();
		JScrollPane scroll = new JScrollPane(routingTable);
		routingListPanel.setBorder(new TitledBorder("·�ɱ�"));
		routingListPanel.setLayout(new BorderLayout());
		JPanel tablepane=new JPanel();
		tablepane.add(scroll);
		timeLabel=new JLabel();
		routingListPanel.add(timeLabel,BorderLayout.NORTH);
		routingListPanel.add(tablepane,BorderLayout.CENTER);
		this.add(routingListPanel,BorderLayout.CENTER);
		//����
		JPanel optPane=new JPanel();
		optPane.setLayout(new BorderLayout());
		optPane.setPreferredSize(new Dimension(0,200));
		JPanel optPanel=new JPanel();
		optPanel.setPreferredSize(new Dimension(120,0));
		optPanel.setBorder(new TitledBorder("�������"));
		JButton btn=new JButton("����·����");
		btn.addActionListener(this);
		optPanel.add(btn);
		optPane.add(optPanel,BorderLayout.WEST);
		JPanel sentInfoPanel=new JPanel();
		sentInfoPanel.setLayout(new BorderLayout());
		sentInfoPanel.setBorder(new TitledBorder("������Ϣ��ʾ���"));
		infoShow=new JTextArea();
		infoShow.setLineWrap(true);
		JScrollPane jspane=new JScrollPane(infoShow);
		sentInfoPanel.add(jspane);
		optPane.add(sentInfoPanel,BorderLayout.CENTER);
		this.add(optPane,BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("����·����")) {
			ConfigDialog.configOne(router.getRouterName(),"����·����",true);
		}
	}
	
}
