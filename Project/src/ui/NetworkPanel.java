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
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import main.Network;

public class NetworkPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * ��Ӧ����
	 */
	private Network network;
	
	/**
	 * ֱ��·�����б�
	 */
	public JList<String> destiRouterList;
	
	/**
	 * ·�ɱ����ʱ���ǩ
	 */
	public JLabel timeLabel;
	
	/**
	 * ·�ɱ�
	 */
	public JTextArea routingShow;
	
	/**
	 * ��Ϣ��ʾ
	 */
	public JTextArea infoShow;
	
	public NetworkPanel(Network n) {
		network=n;
		network.setShowPanel(this);
		this.setLayout(new BorderLayout());
		Init();
	}
	
	private void Init() {
		//����
		this.setBorder(new TitledBorder(network.getnetID().toString()));
		//ֱ�������б�
		JPanel dNetworkListPanel=new JPanel();
		dNetworkListPanel.setPreferredSize(new Dimension(120,0));
		destiRouterList=new JList<String>();
		dNetworkListPanel.setBorder(new TitledBorder("ֱ��·�����б�"));
		dNetworkListPanel.add(destiRouterList);
		this.add(dNetworkListPanel,BorderLayout.WEST);
		//RIP���Ļ�������Ϣ
		JPanel routingListPanel=new JPanel();
		routingListPanel.setPreferredSize(new Dimension(0,200));
		routingShow = new JTextArea();
		routingShow.setLineWrap(true);
		JScrollPane jspane=new JScrollPane(routingShow);
		routingListPanel.setBorder(new TitledBorder("RIP���Ļ�������Ϣ"));
		routingListPanel.setLayout(new BorderLayout());
		timeLabel=new JLabel();
		routingListPanel.add(timeLabel,BorderLayout.NORTH);
		routingListPanel.add(jspane,BorderLayout.CENTER);
		this.add(routingListPanel,BorderLayout.CENTER);
		//����
		JPanel optPane=new JPanel();
		optPane.setLayout(new BorderLayout());
		optPane.setPreferredSize(new Dimension(0,200));
		JPanel optPanel=new JPanel();
		optPanel.setPreferredSize(new Dimension(120,0));
		optPanel.setBorder(new TitledBorder("�������"));
		JButton btn=new JButton("��������");
		btn.addActionListener(this);
		optPanel.add(btn);
		optPane.add(optPanel,BorderLayout.WEST);
		JPanel sentInfoPanel=new JPanel();
		sentInfoPanel.setLayout(new BorderLayout());
		sentInfoPanel.setBorder(new TitledBorder("������Ϣ��ʾ���"));
		infoShow=new JTextArea();
		infoShow.setLineWrap(true);
		JScrollPane jspane2=new JScrollPane(infoShow);
		sentInfoPanel.add(jspane2);
		optPane.add(sentInfoPanel,BorderLayout.CENTER);
		this.add(optPane,BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("��������")) {
			ConfigDialog.configOne(network.getnetID().toString(),"��������",false);
		}
	}

}
