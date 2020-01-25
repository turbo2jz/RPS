package main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import common.IPAddress;
import ui.NetworkPanel;

/**
 * ������
 * @author always
 *
 */
public class Network extends Thread {

	/**
	 * ���缯��Map(��̬)
	 * ((String)Network.netID,Network)
	 * ע������·����ͨ������Ų����������������󴴽�����������ü���
	 */
	public static Map<String,Network> NetworkList;
	
	/**
	 * ����ʱ��
	 * ע�����緢��RIP���ļ��ϵ�ʱ����
	 */
	private static final long SleepTime=(long) 1000;
	
	/**
	 * �����
	 */
	private IPAddress netID;
	
	/**
	 * ֱ��·�����б�
	 * �б����ݣ�(Router.Name)
	 */
	private List<String> destiRouterList;
	
	/**
	 * ֱ��·����״̬����
	 * �б����ݣ�(Router.Name,�Ƿ���������)
	 */
	private Map<String,Boolean> destiRouterMap;
	
	/**
	 * RIP���Ļ�����
	 * �������ݣ�(Router.Name,Router.routingList)
	 */
	private Map<String,List<RoutingListItem>> RIPCache;
	
	/**
	 * ·������Ϣչʾ���
	 */
	private NetworkPanel showPanel;
	
	//���缯�ϳ�ʼ��
	static {
		NetworkList=new HashMap<String,Network>();
	}
	
	/**
	 * ������������缯��Map
	 * @param net
	 */
	public static void addNetwork(Network net) {
		NetworkList.put(net.netID.toString(), net);
	}
	
	/**
	 * Ĭ�Ϲ��캯��
	 */
	public Network() {
		
	}
	
	/**
	 * ���ι��캯��
	 * @param nID �����
	 */
	public Network(IPAddress nID) {
		netID=nID;
		infoListInit();
		addNetwork(this);
	}
	
	/**
	 * ��Ϣ�б��ʼ��
	 */
	private void infoListInit() {
		//ֱ��·�������г�ʼ��
		destiRouterList=new CopyOnWriteArrayList<String>();
		//ֱ��·����״̬���ϳ�ʼ��
		destiRouterMap=new ConcurrentHashMap<String,Boolean>();
		//RIP���Ļ�����г�ʼ��
		RIPCache=new ConcurrentHashMap<String,List<RoutingListItem>>();
	}
	
	/**
	 * ��ȡ���������
	 * @return netID �����
	 */
	public IPAddress getnetID() {
		return netID;
	}
	
	/**
	 * ��ȡֱ��·�����б�
	 * @return destiRouterList ֱ��·�����б�
	 */
	public List<String> getDestiRouterList(){
		return destiRouterList;
	}
	
	/**
	 * ��ȡֱ��·����״̬
	 * @return destiRouterMap
	 */
	public Map<String,Boolean> getDestiRouterMap(){
		return destiRouterMap;
	}
	
	/**
	 * �������������
	 * @param nID �����
	 */
	public void setnetID(IPAddress nID) {
		netID=nID;
	}
	
	/**
	 * ����ֱ��·�����б�
	 * ע���˷�����·�����߳�����ǰ���ã���������ֱ��·�����б�
	 * @param dRouterList ֱ��·�����б�
	 */
	public void setDestiRouterList(List<String> dRouterList) {
		destiRouterList.addAll(dRouterList);
		for(String drouter:destiRouterList) {
			destiRouterMap.put(drouter,true);
		}
	}
	
	/**
	 * ���õ���ֱ��·����
	 * ע�� �˷������������������ǰ����п�����·�������е��ã����Ҫ���в�������
	 * @param routerName ·������
	 */
	public synchronized void setOneDestiRouter(String routerName) {
		destiRouterList.add(routerName);
		destiRouterMap.put(routerName,true);
	}
	
	/**
	 * ����·������Ϣ��ʾ���
	 * @param np ��ʾ���
	 */
	public void setShowPanel(NetworkPanel np) {
		showPanel=np;
	}
	
	/**
	 * ����ֱ��·����״̬
	 * ע����·��������
	 * @param routerName ·������
	 * @param flag �Ƿ���������
	 * @return Boolean �Ƿ�ɹ�����
	 */
	public synchronized Boolean setRouterIsReachable(String routerName, Boolean flag) {
		if(destiRouterList.contains(routerName)) {
			//���������·����״̬�����еĸ�·����Ϊ���ɴ�״̬
			destiRouterMap.replace(routerName, flag);
			//���ø�·����������״̬���ϵĴ�����Ϊ���ɴ�״̬
			Router.RouterList.get(routerName).setNetIsReachableForNet(netID.toString(), flag);
			return true;
		}
		return false;
	}
	
	/**
	 * ���õ�ǰ�����Ƿ���·��������
	 * ע����·��������
	 * @param routerName
	 * @param flag
	 * @return
	 */
	public Boolean setRouterIsReachableForRouter(String routerName,Boolean flag) {
		if(destiRouterMap.containsKey(routerName)) {
			destiRouterMap.replace(routerName, flag);
			return true;
		}
		return false;
	}
	
	/**
	 * ��Ӵ�·������ȡ��RIP������RIPCache
	 * ע���˷�����·����������ã�����ֲ�������this.RIPCache������в������ʿ���
	 * @param sentRouter ����·������
	 * @param rList ·�ɱ�
	 */
	public synchronized void putRIPCache(String sentRouter,List<RoutingListItem> rList) {
		//��Cache���ڸ�·����������RIP���ģ��Ƴ�ԭ����
		if(RIPCache.containsKey(sentRouter)) {
			RIPCache.remove(sentRouter);
		}
		//�������
		RIPCache.put(sentRouter, rList);
	}
	
	/**
	 * ��ȡר����ĳһ·������RIP���ļ���
	 * ע��ȥ���ɸ�·�����Լ����͵ı��ģ��˴����ȡRIPCache������в������ʿ���
	 * @param dRouterName ·������
	 * @return Map< String,List< RoutingListItem > > ר��RIP���ļ���
	 */
	private synchronized Map<String,List<RoutingListItem>> getDedicRIPCache(String dRouterName) {
		//��ʱ����result
		Map<String,List<RoutingListItem>> result=new ConcurrentHashMap<String,List<RoutingListItem>>();
		result.putAll(RIPCache);
		//�Ƴ���·����dRouterName���͵ı���
		result.remove(dRouterName);
		return result;
	}
	
	/**
	 * ���ͻ�ȡRIP���ļ������ڽ�·����RIPCache
	 * ע���˷�������·�������putRIPCache�������������绺���IP���ķ�������
	 * �����ڽӵ�·����
	 */
	private void sentSelfRIP() { 
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		showPanel.infoShow.append(df.format(new Date())+"\n");
		for(String dRouterName:destiRouterList) {
			if(destiRouterMap.get(dRouterName)) {
				Router router=Router.RouterList.get(dRouterName);
				router.putRIPCache(this.getDedicRIPCache(router.getRouterName()));
				showPanel.infoShow.append(netID.toString()+" --> "+dRouterName+"\n");
			}
			else {
				showPanel.infoShow.append(netID.toString()+" --X "+dRouterName+"\n");
			}
		}
		showPanel.infoShow.append("\n");
	}
	
	/**
	 * �������ʾ��Ϣ
	 */
	private synchronized void showInfo() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		showPanel.timeLabel.setText(df.format(new Date()));
		DefaultListModel<String> model=new DefaultListModel<String>();
		for(String rname:destiRouterList) {
			model.addElement(rname);
		}
		showPanel.destiRouterList.setModel(model);
		StringBuilder sb=new StringBuilder();
		for(Map.Entry<String,List<RoutingListItem>> entry : RIPCache.entrySet()) {
			sb.append(entry.getKey()+"_RIP���ģ�\n");
			List<RoutingListItem> tList=entry.getValue();
			sb.append(String.format("%s\t%s\t%s\n","Ŀ������","��һ��·����","����"));
			for(RoutingListItem item:tList) {
				sb.append(item.toString()+"\n");
			}
		}
		showPanel.routingShow.setText(sb.toString());
		showPanel.updateUI();
	}
	
	@Override
	public void run() {
		while(true) {
			//�����ʾ
			showInfo();
			//����RIP���Ļ�����·����
			sentSelfRIP();
			try {
				Thread.sleep(SleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
