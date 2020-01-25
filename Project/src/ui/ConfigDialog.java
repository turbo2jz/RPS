package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import common.CheckBoxList;
import main.Network;
import main.Router;

/**
 * ���öԻ���
 * @author always
 *
 */
public class ConfigDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * �������
	 */
	private final JPanel contentPanel = new JPanel();
	
	/**
	 * ��ѡ�򼯺�
	 */
	private CheckBoxList dStateList;
	
	/**
	 * ������
	 */
	private JComboBox<String> nameList;
	
	/**
	 * ·����(����)��ʶ
	 * true--router false--net
	 */
	private Boolean routerOrNet=false;
	
	/**
	 * ����˵�����ť(����·����/����ʱ)����
	 * @param title
	 * @param flag
	 */
	public static void configAll(String title,Boolean flag) {
		ConfigDialog dialog = new ConfigDialog(flag);
		dialog.setTitle(title);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setFocusable(true);
		dialog.setVisible(true);
	}
	
	/**
	 * ���·����(������)������ð�ť����
	 * @param name
	 * @param title
	 * @param flag
	 */
	public static void configOne(String name,String title,Boolean flag) {
		ConfigDialog dialog = new ConfigDialog(flag);
		dialog.setTitle(title);
		dialog.nameList.setSelectedItem(name);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setFocusable(true);
		dialog.setVisible(true);
	}
	
	/**
	 * ���캯��
	 */
	public ConfigDialog(Boolean flag) {
		setModal(true);
		routerOrNet=flag;
		setSize(174, 219);
		setLocationRelativeTo(null);
		Image icon = Toolkit.getDefaultToolkit().getImage("icon/icon.png"); 
        setIconImage(icon);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		dStateList=new CheckBoxList();
		nameList=new JComboBox<String>();
		if(routerOrNet) {
			for(Map.Entry<String, Router> entry:Router.RouterList.entrySet()) {
				nameList.addItem(entry.getKey());
			}
		}
		else {
			for(Map.Entry<String, Network> entry:Network.NetworkList.entrySet()) {
				nameList.addItem(entry.getKey());
			}
		}
		nameList.setSelectedIndex(0);
		nameList.addActionListener(this);
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JPanel InfoPane = new JPanel();
		InfoPane.setLayout(new BoxLayout(InfoPane,BoxLayout.Y_AXIS));
		InfoPane.add(new JLabel((routerOrNet?"·����":"����")));
		InfoPane.add(nameList);
		JPanel dNetStatePane=new JPanel();
		dNetStatePane.setLayout(new BorderLayout());
		dNetStatePane.setBorder(new TitledBorder("ֱ��"+(routerOrNet?"·����":"����")+"״̬"));
		dNetStatePane.add(dStateList);
		InfoPane.add(dNetStatePane);
		contentPanel.add(InfoPane);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("ȷ��");
		okButton.addActionListener(this);
		buttonPane.add(okButton);
		JButton cancelButton = new JButton("ȡ��");
		cancelButton.addActionListener(this);;
		buttonPane.add(cancelButton);
	}
	
	/**
	 * ����·��������
	 * @param routerName
	 */
	private void routerDealing(String routerName) {
		Router r=Router.RouterList.get(routerName);
		Map<String,Boolean> Itemset=dStateList.getAllItem();
		for(Map.Entry<String,Boolean>entry:Itemset.entrySet()) {
			r.setNetIsReachable(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * �������紦��
	 * @param netId
	 */
	private void networkDealing(String netId) {
		Network n=Network.NetworkList.get(netId);
		Map<String,Boolean> Itemset=dStateList.getAllItem();
		for(Map.Entry<String,Boolean>entry:Itemset.entrySet()) {
			n.setRouterIsReachable(entry.getKey(), entry.getValue());
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("ȷ��")) {
			if(nameList.getSelectedIndex()!=-1) {
				if(routerOrNet)
					routerDealing((String) nameList.getSelectedItem());
				else
					networkDealing((String) nameList.getSelectedItem());
				JOptionPane.showMessageDialog(this, "���ý������뷵��������鿴��Ϣ��",
						"��ʾ",JOptionPane.INFORMATION_MESSAGE);
				this.dispose();
			}
			else {
				JOptionPane.showMessageDialog(this, "��δ����ѡ��",
						"��ʾ",JOptionPane.WARNING_MESSAGE);
				return;
			}
		}
		
		if(e.getActionCommand().equals("ȡ��")) {
			this.dispose();
		}
		
		if(e.getSource()==nameList) {
			if(routerOrNet)
				dStateList.Init(Router.RouterList.get(nameList.getSelectedItem()).getDestiNetworkMap());
			else
				dStateList.Init(Network.NetworkList.get(nameList.getSelectedItem()).getDestiRouterMap());
		}
		
	}

}
