package main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;


import ui.RouterPanel;

/**
 * ·������
 * @author always
 *
 */
public class Router extends Thread {
	
	/**
	 * ·��������Map(��̬)
	 * ��ʽ��(Router.routerName,Router)
	 * ע����������ͨ��·����������·��������·�������󴴽�����������ü���
	 */
	public static Map<String,Router> RouterList;
	
	/**
	 * ֱ��������
	 * ע��ֱ�������·�ɱ�����һ��·������Ϊ����
	 */
	private static final String DirectFlag="Direct";
	
	/**
	 * ����ʱ��
	 * ע��·��������RIP����ʱ����
	 */
	private static final long SleepTime=(long) 1000;
	
	/**
	 * ��ʱ������ʱʱ��
	 */
	private static final long taskDelayTime=(long) 2000;

	/**
	 * ·������
	 */
	private String routerName;
	
	/**
	 * ֱ�������б�
	 * (netID��ʶ)
	 */
	private List<String> destiNetworkList;
	
	/**
	 * ֱ������״̬����
	 * ���ڱ�ʶ�Ƿ���ֱ��������������
	 * ��ʽ��(�����,�Ƿ���������)
	 */
	private Map<String,Boolean> destiNetworkMap;
	
	/**
	 * ·�ɱ�
	 */
	private List<RoutingListItem> routingList;
	
	/**
	 * δ����RIP���ļ���
	 * ��ʽ��(SentRouter.Name,RouteringList)
	 */
	private Map<String,List<RoutingListItem>> RIPCache;
	
	/**
	 * ��ʱ��
	 * ���ڼ����������״̬�������������ģ��
	 */
	private Timer timer;
	
	/**
	 * ·������Ϣչʾ���
	 */
	private RouterPanel showPanel;
	
	
	//·�������ϳ�ʼ��
	static {
		RouterList=new HashMap<String,Router>();
	}
	
	/**
	 * ���·������·��������Map
	 * @param router
	 */
	public static void addRouter(Router router) {
		RouterList.put(router.getRouterName(),router);
	}
	
	/**
	 * Ĭ�Ϲ��캯��
	 */
	public Router() {
		
	}
	
	/**
	 * ���ι��캯��
	 * @param rName ·������
	 */
	public Router(String rName) {
		routerName=rName;
		infoListInit();
		timerInit();
		addRouter(this);
	}
	
	/**
	 * ��Ϣ�б��ʼ��
	 */
	private void infoListInit() {
		//ֱ�������б��ʼ��
		destiNetworkList=new CopyOnWriteArrayList<String>();
		//ֱ����������״̬���ϳ�ʼ��
		destiNetworkMap=new ConcurrentHashMap<String,Boolean>();
		//·�ɱ��ʼ��
		routingList=new CopyOnWriteArrayList<RoutingListItem>();
		//RIP���Ķ��г�ʼ��
		RIPCache=new ConcurrentHashMap<String,List<RoutingListItem>>();
	}
	
	/**
	 * ��ʱ�������ֱ�������Ƿ������ɴ������·�ɱ����
	 */
	private void timerInit() {
		timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Router.this.checkDestiNetwork();
			}	
	    }, 0, taskDelayTime);
	}
	
	/**
	 * ��ȡ·������
	 * @return
	 */
	public String getRouterName() {
		return routerName;
	}
	
	/**
	 * ��ȡֱ�������б�
	 * @return
	 */
	public List<String> getDestiNetworkList() {
		return destiNetworkList;
	}
	
	/**
	 * ��ȡֱ������״̬��
	 * @return
	 */
	public Map<String,Boolean> getDestiNetworkMap() {
		return destiNetworkMap;
	}
	
	/**
	 * ����·������
	 * @param Name
	 */
	public void setRouterName(String Name) {
		routerName=Name;
	}
	
	/**
	 * ����ֱ�������
	 * @param dNetworkList
	 */
	public void setdestiNetworkList(List<String> dNetworkList) {
		destiNetworkList.addAll(dNetworkList);
		//��ʼ��Ϊ��������
		for(String dnet:destiNetworkList) {
			destiNetworkMap.put(dnet,true);
		}
		//��ʼ��·�ɱ�
		setroutingList();
	}
	
	/**
	 * ��ʼ��·�ɱ�
	 * ע��˽�з�������������ֱ�������б���Զ�����
	 */
	private void setroutingList() {
		for(String dNetworkID:destiNetworkList) {
			routingList.add(new RoutingListItem(dNetworkID,DirectFlag,1));
		}
	}
	
	/**
	 * ���õ���ֱ������
	 * ע���˷�����·�����߳����к���ܵ��ã�����漰ͬʱ����destiNetworkList��routingList
	 * ����в�������
	 */
	public synchronized void setOnetDestiNetwork(String Ip) {
		destiNetworkList.add(Ip);
		destiNetworkMap.put(Ip, true);
		routingList.add(new RoutingListItem(Ip,DirectFlag,1));
		//���������෽������������·��������ֱ֪��
		Network n=Network.NetworkList.get(Ip);
		n.setOneDestiRouter(routerName);
	}
	
	/**
	 * ����·������Ϣ��ʾ���
	 * @param rp
	 */
	public void setShowPanel(RouterPanel rp) {
		showPanel=rp;
	}
	
	/**
	 * ��ָ��ֱ������״̬
	 * @param Ip
	 * @param isReachable
	 * @return
	 */
	public synchronized Boolean setNetIsReachable(String Ip,Boolean isReachable) {
		//����ֱ������ʱ�޸ģ�����true
		if(destiNetworkMap.containsKey(Ip)) {
			//����·����������״̬���ϵĴ�����Ϊ���ɴ�״̬
			destiNetworkMap.replace(Ip, isReachable);
			if(isReachable) 
				setRoutringItemReachable(Ip);//���ô˷�����������·����Ϊֱ��
			//���������·����״̬�����еĴ�·����Ϊ���ɴ�״̬
			Network.NetworkList.get(Ip).setRouterIsReachableForRouter(routerName, isReachable);
			return true;
		}
		//�����ڷ���false
		return false;
	}
	
	/**
	 * ���õ�ǰ·�����Ƿ�����������
	 * ע�����������
	 * @param netId
	 * @param flag
	 * @return
	 */
	public Boolean setNetIsReachableForNet(String netId,Boolean flag) {
		if(destiNetworkMap.containsKey(netId)) {
			destiNetworkMap.replace(netId, flag);
			return true;
		}
		return false;
	}
	
	/**
	 * ���ֱ�����磬��Ϊ���ɴ��޸�·�ɱ�
	 */
	public synchronized void checkDestiNetwork() {
		for(String ip:destiNetworkList) {
			if(!destiNetworkMap.get(ip)) {
				//���ô˷�����δ����RIP���Ļ��������Ϊ���ɴ������һ��·����Ϊ�������ڽ�·������·�ɱ���Ϊ���ɴ�
				setRoutringItemUnreachable(ip);
				showPanel.infoShow.append(ip+"δ������ʱ���ڷ���RIP������Ϣ������Ϊ���ɴ\n");
			}
		}
	}
	
	/**
	 * (�ָ���������ʱ����)
	 * ˽�з���
	 */
	private void setRoutringItemReachable(String Ip) {
		for(RoutingListItem item:routingList) {
			if(item.getDestiNetwork().equals(Ip)) {
				item.setNextRouter(DirectFlag);
				item.setHopNum(1);
				break;
			}
		}
	}
	
	/**
	 * (ģ�����ʱ����)
	 * ˽�з���
	 * @param Ip
	 * @param isReachable
	 */
	private void setRoutringItemUnreachable(String Ip) {
		List<String> nr=Network.NetworkList.get(Ip).getDestiRouterList();
		for(RoutingListItem item:routingList) {
			if(item.getDestiNetwork().equals(Ip)) {
				item.setHopNum(RoutingListItem.maxHopNum);
				break;
			}
			for(String n:nr) {
				if(item.getNextRouter().equals(n)) {
					item.setHopNum(RoutingListItem.maxHopNum);
					break;
				}
			}
		}
	}
	
	/**
	 * ��Ӵ������ȡ�����ڽ�·�������͵�RIP���ļ���
	 * ע���˷����������������ã�����ֲ�������this.RIPCache������в������ʿ���
	 * @param nRIPCache
	 */
	public synchronized void putRIPCache(Map<String,List<RoutingListItem>> nRIPCache) {
		//�˴�����ֱ�Ӷ�nRIPCacheʹ�ý���RIPCache.putAll��������ʱ���������ɸ���·��������·�ɱ��ʵ����
		//D_V�㷨�еı����޸Ĳ�����ֱ���޸�ԭ·����·�ɱ��������Ī������Ĵ���
		for(Map.Entry<String,List<RoutingListItem>> entry : nRIPCache.entrySet()) {
			//Key--·������
			String sentRouterName=entry.getKey();
			//Value--��Ӧ·�ɱ�
			List<RoutingListItem> sentRoutingList=entry.getValue();
			//����·�ɱ�
			List<RoutingListItem> copyRoutingList=new CopyOnWriteArrayList<RoutingListItem>();
			for(RoutingListItem sitem:sentRoutingList) {
				copyRoutingList.add(new RoutingListItem(sitem));
			}
			RIPCache.put(sentRouterName,copyRoutingList);
		}
	}
	
	/**
	 * ���ͻ�ȡRIP�������ڽ������RIPCache
	 * ע���˷��������������putRIPCache����������·����·�ɱ��װ��RIP���ķ�������
	 * ·�����ڽӵ�����
	 */
	private void sentSelfRIP() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		showPanel.infoShow.append(df.format(new Date())+"\n");
		//����ֱ�������б�
		for(String dNetworkID : destiNetworkList) {
			//�����������������緢��
			if(destiNetworkMap.get(dNetworkID)) {
				//����netID��ȡNetwork����
				Network dNetwork=Network.NetworkList.get(dNetworkID);
				//���ͱ�·����routingList������
				//�˴�Ӧ�÷��͸��ƺ��routingList
				//����·�ɱ�
				List<RoutingListItem> copyRoutingList=new CopyOnWriteArrayList<RoutingListItem>();
				for(RoutingListItem sitem:routingList) {
					copyRoutingList.add(new RoutingListItem(sitem));
				}
				dNetwork.putRIPCache(routerName, copyRoutingList);
				showPanel.infoShow.append(routerName+" --> "+dNetworkID+"\n");
			}
			else {
				showPanel.infoShow.append(routerName+" --X "+dNetworkID+"\n");
			}
		}
		showPanel.infoShow.append("\n");
	}
	
	/**
	 * RIP���������㷨
	 * ע���轫RIPCache������RIP���Ľ����㷨����D_V�㷨���ռRIPCache,routingList
	 */
	private synchronized void D_V() {
		//����δ����RIP���ļ���
		for(Map.Entry<String,List<RoutingListItem>> entry : RIPCache.entrySet()) {
			//Key--·������
			String sentRouterName=entry.getKey();
			//Value--��Ӧ·�ɱ�
			List<RoutingListItem> sentRoutingList=entry.getValue();
			
			for(RoutingListItem sitem:sentRoutingList) {
				//·�ɱ���
				//����һ��·����Ϊ����·����
				sitem.setNextRouter(sentRouterName);
				//������1
				sitem.increHopNum();
				
				//�洢Ŀ��������ͬ��routingListItem'Index
				int index=-1;
				
				for(int i=0;i<routingList.size();i++) {
					RoutingListItem titem=routingList.get(i);
					//�ж�routingList�Ƿ���������Ŀ��������ͬ���������indexΪi������ѭ��
					if(titem.equalsDNet(sitem)) {
						index=i;
						break;
					}
				}
				
				//routingList���������Ŀ��������ͬ����
				if(index!=-1) {
					RoutingListItem titem=routingList.get(index);
					//�ж��Ƿ�����ͬ��һ��·����
					if(titem.equalsNRouter(sitem)) {
						//����ͬ��һ��·����
						//�滻ԭ��Item
						routingList.remove(index);
						routingList.add(sitem);
					}
					else {
						//�Ƚ�����
						if(sitem.getHopNum()<titem.getHopNum()) {
							//����С��ԭ����
							//�滻ԭ��Item
							routingList.remove(index);
							routingList.add(sitem);
						}
					}
				}
				//������--��Ӹ���Ŀ
				else {
					routingList.add(sitem);
				}
			}
		}
		//������ɺ����RIPCache
		RIPCache.clear();
	}
	
	/**
	 * ·�ɱ���Ϣ���
	 * ע������㷨��Ҫ��ռroutingList
	 * @param str ������Ϣ��ʶ���λ��(�ڸ���ǰ���Ǻ�)
	 */
	public void print(String str) {
		StringBuffer sb=new StringBuffer();
		sb.append("·��������"+routerName+"\n");
		sb.append(str+"\n");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sb.append(df.format(new Date())+"\n");
		sb.append(String.format("%15s%12s%5s\n","Ŀ������","��һ��·����","����"));
		for(RoutingListItem i:routingList) {
			sb.append(i.toString()+"\n");
		}
		System.out.println(sb.toString());
	}
	
	/**
	 * �������ʾ��Ϣ
	 */
	private void showInfo() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		showPanel.timeLabel.setText(df.format(new Date()));
		DefaultListModel<String> lmodel=new DefaultListModel<String>();
		for(String nname:destiNetworkList) {
			lmodel.addElement(nname);
		}
		showPanel.destiNetworkList.setModel(lmodel);
		String[] title= {"Ŀ������","��һ��·����","����"};
		DefaultTableModel model=new DefaultTableModel(title,0);
		for(RoutingListItem i:routingList) {
			if(i!=null) {
				Object[] info=new Object[3];
				info[0]=i.getDestiNetwork();
				info[1]=i.getNextRouter();
				info[2]=i.getHopNum();
				model.addRow(info);
			}	
		}
		showPanel.routingTable.setModel(model);
	}
	
	@Override
	public void run() {
		while(true) {
			//����̨�������
			//print("����ǰ·�ɱ�");
			showInfo();
			//����RIP����������
			sentSelfRIP();
			try {
				Thread.sleep(SleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//���������㷨
			D_V();
			//print("���º�·�ɱ�");
		}
	}
	
}
