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
	 * 对应网络
	 */
	private Network network;
	
	/**
	 * 直连路由器列表
	 */
	public JList<String> destiRouterList;
	
	/**
	 * 路由表更新时间标签
	 */
	public JLabel timeLabel;
	
	/**
	 * 路由表
	 */
	public JTextArea routingShow;
	
	/**
	 * 信息显示
	 */
	public JTextArea infoShow;
	
	public NetworkPanel(Network n) {
		network=n;
		network.setShowPanel(this);
		this.setLayout(new BorderLayout());
		Init();
	}
	
	private void Init() {
		//标题
		this.setBorder(new TitledBorder(network.getnetID().toString()));
		//直连网络列表
		JPanel dNetworkListPanel=new JPanel();
		dNetworkListPanel.setPreferredSize(new Dimension(120,0));
		destiRouterList=new JList<String>();
		dNetworkListPanel.setBorder(new TitledBorder("直连路由器列表"));
		dNetworkListPanel.add(destiRouterList);
		this.add(dNetworkListPanel,BorderLayout.WEST);
		//RIP报文缓冲区信息
		JPanel routingListPanel=new JPanel();
		routingListPanel.setPreferredSize(new Dimension(0,200));
		routingShow = new JTextArea();
		routingShow.setLineWrap(true);
		JScrollPane jspane=new JScrollPane(routingShow);
		routingListPanel.setBorder(new TitledBorder("RIP报文缓冲区信息"));
		routingListPanel.setLayout(new BorderLayout());
		timeLabel=new JLabel();
		routingListPanel.add(timeLabel,BorderLayout.NORTH);
		routingListPanel.add(jspane,BorderLayout.CENTER);
		this.add(routingListPanel,BorderLayout.CENTER);
		//配置
		JPanel optPane=new JPanel();
		optPane.setLayout(new BorderLayout());
		optPane.setPreferredSize(new Dimension(0,200));
		JPanel optPanel=new JPanel();
		optPanel.setPreferredSize(new Dimension(120,0));
		optPanel.setBorder(new TitledBorder("操作面板"));
		JButton btn=new JButton("配置网络");
		btn.addActionListener(this);
		optPanel.add(btn);
		optPane.add(optPanel,BorderLayout.WEST);
		JPanel sentInfoPanel=new JPanel();
		sentInfoPanel.setLayout(new BorderLayout());
		sentInfoPanel.setBorder(new TitledBorder("发送信息显示面板"));
		infoShow=new JTextArea();
		infoShow.setLineWrap(true);
		JScrollPane jspane2=new JScrollPane(infoShow);
		sentInfoPanel.add(jspane2);
		optPane.add(sentInfoPanel,BorderLayout.CENTER);
		this.add(optPane,BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("配置网络")) {
			ConfigDialog.configOne(network.getnetID().toString(),"配置网络",false);
		}
	}

}
