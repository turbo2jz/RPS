package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import common.IPAddress;
import common.IPType;
import main.Network;
import main.Router;


/**
 * 主窗口
 * @author always
 *
 */
public class MainFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 网络拓扑文件存储路径
	 */
	private static final String dataPath="data/topo.txt";
	
	/**
	 * 容器面板
	 */
	private JPanel contentPane;
	
	/**
	 * 拓扑信息显示面板
	 */
	private JPanel topoInfoPanel;
	
	/**
	 * 拓扑信息树
	 */
	private JTree topoTree;
	
	/**
	 * 路由器信息显示面板
	 */
	private JPanel routerInfoPanel;
	
	/**
	 * 网络信息显示面板
	 */
	private JPanel networkInfoPanel;

	private JPanel showPane;
	
	private List<Router> routerList;
	private List<RouterPanel> routerPanelList;
	
	private List<Network> networkList;
	private List<NetworkPanel> networkPanelList;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("路由协议模拟软件");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1031, 684);
		setLocationRelativeTo(null);
		Image icon = Toolkit.getDefaultToolkit().getImage("icon/icon.png"); 
        setIconImage(icon);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		panelInit();
		listInit();
	}
	
	private void panelInit() {
		//工具栏初始化
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		contentPane.add(toolBar, BorderLayout.NORTH);
		//工具栏--按钮
		JButton button = new JButton("加载网络拓扑");
		button.addActionListener(this);
		JButton button1 = new JButton("配置路由器");
		button1.addActionListener(this);
		JButton button2 = new JButton("配置网络");
		button2.addActionListener(this);
		JButton button3 = new JButton("添加路由器");
		button3.addActionListener(this);
		JButton button4 = new JButton("添加网络");
		button4.addActionListener(this);
		toolBar.add(button);
		toolBar.add(button1);
		toolBar.add(button2);
		toolBar.add(button3);
		toolBar.add(button4);

		//路由器信息显示块
		routerInfoPanel=new JPanel();
		routerInfoPanel.setLayout(new BoxLayout(routerInfoPanel,BoxLayout.Y_AXIS));
		JPanel routerInfoPane=new JPanel();
		routerInfoPane.setLayout(new BorderLayout());
		routerInfoPane.setBorder(new TitledBorder("路由器"));
		//routerInfoPane.add(routerInfoPanel);
		routerInfoPane.add(new JScrollPane(routerInfoPanel));
		//网络信息显示块
		networkInfoPanel=new JPanel();
		networkInfoPanel.setLayout(new BoxLayout(networkInfoPanel,BoxLayout.Y_AXIS));
		JPanel networkInfoPane=new JPanel();
		networkInfoPane.setLayout(new BorderLayout());
		networkInfoPane.setBorder(new TitledBorder("网络"));
		//networkInfoPane.add(networkInfoPanel);
		networkInfoPane.add(new JScrollPane(networkInfoPanel));
		//将上述两面板置于窗口中部
		showPane=new JPanel();
		showPane.setBorder(new TitledBorder("路由信息"));
		showPane.setLayout(new BorderLayout());
		showPane.add(routerInfoPane,BorderLayout.NORTH);
		showPane.add(networkInfoPane,BorderLayout.SOUTH);
		JSplitPane Split=new JSplitPane(JSplitPane.VERTICAL_SPLIT,routerInfoPane,networkInfoPane);
		showPane.add(Split);
		contentPane.add(showPane,BorderLayout.CENTER);
		//拓扑信息显示面板
		topoInfoPanel=new JPanel();
		topoInfoPanel.setBorder(new TitledBorder("拓扑信息"));
		topoInfoPanel.setLayout(new BorderLayout());
		topoInfoPanel.setPreferredSize(new Dimension(200,0));
		contentPane.add(topoInfoPanel,BorderLayout.WEST);
	}
	
	/**
	 * 列表初始化
	 */
	private void listInit() {
		routerList=new ArrayList<Router>();
		routerPanelList=new ArrayList<RouterPanel>();
		networkList=new ArrayList<Network>();
		networkPanelList=new ArrayList<NetworkPanel>();
	}
	
	/**
	 * 加载网络拓扑
	 * @throws IOException 
	 */
	public void loadNetToplogy() throws IOException {
		//读取全部行
		FileReader fr = new FileReader(dataPath);
		BufferedReader br = new BufferedReader(fr);
		List<String> allLines = new ArrayList<String>();
		String Line;
		while((Line=br.readLine())!=null){
			allLines.add(Line);
		}
		br.close();
		fr.close();
		
		//获取分割线下标
		int index1=0,index2=0;
		//截取Router信息
		index1=allLines.indexOf("-");
		List<String> RouterName=allLines.subList(0, index1);
		for(String rn:RouterName) {
			routerList.add(new Router(rn));
		}
		//截取网络信息
		index2=allLines.lastIndexOf("-");
		List<String> NetworkIp=allLines.subList(index1+1, index2);
		for(String rn:NetworkIp) {
			networkList.add(new Network(new IPAddress(rn,IPType.netid)));
		}
		//截取配置信息
		List<String> topoInfo=allLines.subList(index2+1, allLines.size());
		for(int i=0;i<topoInfo.size();i++) {
			String[] InfoStrs=topoInfo.get(i).split("/");
			for(String ipStr:InfoStrs) {
				routerList.get(i).setOnetDestiNetwork(ipStr);
			}
		}
	}
	
	/**
	 * 显示网络拓扑信息
	 */
	private void showTopoInfo() {
		DefaultMutableTreeNode routerFatherNode=new DefaultMutableTreeNode("Router");
		for(Router r:routerList) {
			DefaultMutableTreeNode routerNode=new DefaultMutableTreeNode(r.getRouterName());
			List<String> dNetList=r.getDestiNetworkList();
			for(String netId:dNetList) {
				routerNode.add(new DefaultMutableTreeNode(netId));
			}
			routerFatherNode.add(routerNode);
		}
		DefaultMutableTreeNode netFatherNode=new DefaultMutableTreeNode("Network");
		for(Network n:networkList) {
			DefaultMutableTreeNode netNode=new DefaultMutableTreeNode(n.getnetID().toString());
			List<String> dRouterList=n.getDestiRouterList();
			for(String rName:dRouterList) {
				netNode.add(new DefaultMutableTreeNode(rName));
			}
			netFatherNode.add(netNode);
		}
		DefaultMutableTreeNode FatherNode=new DefaultMutableTreeNode("TopoInfo");
		FatherNode.add(routerFatherNode);
		FatherNode.add(netFatherNode);
		topoTree=new JTree(FatherNode);
		topoInfoPanel.add(topoTree);
	}
	
	/**
	 * 显示路由器及网络信息
	 */
	private void showRouterAndNetInfo() {
		for(Router r:routerList) {
			RouterPanel rp=new RouterPanel(r);
			rp.setVisible(true);
			routerPanelList.add(rp);
			routerInfoPanel.add(rp);
		}
		for(Network n:networkList) {
			NetworkPanel np=new NetworkPanel(n);
			np.setVisible(true);
			networkPanelList.add(np);
			networkInfoPanel.add(np);
		}
		contentPane.updateUI();
		showPane.updateUI();
		routerInfoPanel.updateUI();
		networkInfoPanel.updateUI();
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().equals("加载网络拓扑")) {
			if(!routerList.isEmpty()&&!networkList.isEmpty()) {
				JOptionPane.showMessageDialog(this, "您已加载过网络拓扑信息！",
						"提示",JOptionPane.WARNING_MESSAGE);
				return;
			}
			try {
				loadNetToplogy();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			showRouterAndNetInfo();
			showTopoInfo();
			//启动Router
			for(Router r:routerList) {
				r.start();
			}
			//启动Network
			for(Network n:networkList) {
				n.start();
			}
		}
		
		if(e.getActionCommand().equals("配置路由器")) {
			ConfigDialog.configAll("配置路由器",true);
		}
		
		if(e.getActionCommand().equals("配置网络")) {
			ConfigDialog.configAll("配置网络",false);
		}
		
		if(e.getActionCommand().equals("添加路由器")) {
			JOptionPane.showMessageDialog(this, "添加路由器功能未实现！！！",
					"提示",JOptionPane.INFORMATION_MESSAGE);
		}
		
		if(e.getActionCommand().equals("添加网络")) {
			JOptionPane.showMessageDialog(this, "添加网络功能未实现！！！",
					"提示",JOptionPane.INFORMATION_MESSAGE);
		}
		
	}

}
