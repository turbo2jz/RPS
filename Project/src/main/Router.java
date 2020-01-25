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
 * 路由器类
 * @author always
 *
 */
public class Router extends Thread {
	
	/**
	 * 路由器集合Map(静态)
	 * 格式：(Router.routerName,Router)
	 * 注：用于网络通过路由器名查找路由器对象，路由器对象创建即被添加至该集合
	 */
	public static Map<String,Router> RouterList;
	
	/**
	 * 直连网络标记
	 * 注：直接网络的路由表项下一跳路由器置为此项
	 */
	private static final String DirectFlag="Direct";
	
	/**
	 * 休眠时间
	 * 注：路由器发送RIP报文时间间隔
	 */
	private static final long SleepTime=(long) 1000;
	
	/**
	 * 定时任务延时时间
	 */
	private static final long taskDelayTime=(long) 2000;

	/**
	 * 路由器名
	 */
	private String routerName;
	
	/**
	 * 直连网络列表
	 * (netID标识)
	 */
	private List<String> destiNetworkList;
	
	/**
	 * 直连网络状态集合
	 * 用于标识是否与直连网络正常相连
	 * 格式：(网络号,是否正常连接)
	 */
	private Map<String,Boolean> destiNetworkMap;
	
	/**
	 * 路由表
	 */
	private List<RoutingListItem> routingList;
	
	/**
	 * 未处理RIP报文集合
	 * 格式：(SentRouter.Name,RouteringList)
	 */
	private Map<String,List<RoutingListItem>> RIPCache;
	
	/**
	 * 定时器
	 * 用于检查网络连接状态，用于网络故障模拟
	 */
	private Timer timer;
	
	/**
	 * 路由器信息展示面板
	 */
	private RouterPanel showPanel;
	
	
	//路由器集合初始化
	static {
		RouterList=new HashMap<String,Router>();
	}
	
	/**
	 * 添加路由器至路由器集合Map
	 * @param router
	 */
	public static void addRouter(Router router) {
		RouterList.put(router.getRouterName(),router);
	}
	
	/**
	 * 默认构造函数
	 */
	public Router() {
		
	}
	
	/**
	 * 含参构造函数
	 * @param rName 路由器名
	 */
	public Router(String rName) {
		routerName=rName;
		infoListInit();
		timerInit();
		addRouter(this);
	}
	
	/**
	 * 信息列表初始化
	 */
	private void infoListInit() {
		//直连网络列表初始化
		destiNetworkList=new CopyOnWriteArrayList<String>();
		//直连网络连接状态集合初始化
		destiNetworkMap=new ConcurrentHashMap<String,Boolean>();
		//路由表初始化
		routingList=new CopyOnWriteArrayList<RoutingListItem>();
		//RIP报文队列初始化
		RIPCache=new ConcurrentHashMap<String,List<RoutingListItem>>();
	}
	
	/**
	 * 定时检查所有直连网络是否正常可达，并进行路由表更改
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
	 * 获取路由器名
	 * @return
	 */
	public String getRouterName() {
		return routerName;
	}
	
	/**
	 * 获取直连网络列表
	 * @return
	 */
	public List<String> getDestiNetworkList() {
		return destiNetworkList;
	}
	
	/**
	 * 获取直连网络状态表
	 * @return
	 */
	public Map<String,Boolean> getDestiNetworkMap() {
		return destiNetworkMap;
	}
	
	/**
	 * 设置路由器名
	 * @param Name
	 */
	public void setRouterName(String Name) {
		routerName=Name;
	}
	
	/**
	 * 设置直连网络表
	 * @param dNetworkList
	 */
	public void setdestiNetworkList(List<String> dNetworkList) {
		destiNetworkList.addAll(dNetworkList);
		//初始化为正常连接
		for(String dnet:destiNetworkList) {
			destiNetworkMap.put(dnet,true);
		}
		//初始化路由表
		setroutingList();
	}
	
	/**
	 * 初始化路由表
	 * 注：私有方法，在设置完直连网络列表后自动调用
	 */
	private void setroutingList() {
		for(String dNetworkID:destiNetworkList) {
			routingList.add(new RoutingListItem(dNetworkID,DirectFlag,1));
		}
	}
	
	/**
	 * 设置单个直连网络
	 * 注：此方法在路由器线程运行后可能调用，因此涉及同时访问destiNetworkList，routingList
	 * 需进行并发控制
	 */
	public synchronized void setOnetDestiNetwork(String Ip) {
		destiNetworkList.add(Ip);
		destiNetworkMap.put(Ip, true);
		routingList.add(new RoutingListItem(Ip,DirectFlag,1));
		//调用网络类方法，置网络与路由器互相知直连
		Network n=Network.NetworkList.get(Ip);
		n.setOneDestiRouter(routerName);
	}
	
	/**
	 * 设置路由器信息显示面板
	 * @param rp
	 */
	public void setShowPanel(RouterPanel rp) {
		showPanel=rp;
	}
	
	/**
	 * 置指定直连网络状态
	 * @param Ip
	 * @param isReachable
	 * @return
	 */
	public synchronized Boolean setNetIsReachable(String Ip,Boolean isReachable) {
		//存在直连网络时修改，返回true
		if(destiNetworkMap.containsKey(Ip)) {
			//设置路由器的网络状态集合的此网络为不可达状态
			destiNetworkMap.replace(Ip, isReachable);
			if(isReachable) 
				setRoutringItemReachable(Ip);//调用此方法置网络与路由器为直连
			//设置网络的路由器状态集合中的此路由器为不可达状态
			Network.NetworkList.get(Ip).setRouterIsReachableForRouter(routerName, isReachable);
			return true;
		}
		//不存在返回false
		return false;
	}
	
	/**
	 * 设置当前路由器是否与网络连接
	 * 注：由网络调用
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
	 * 检查直连网络，若为不可达修改路由表
	 */
	public synchronized void checkDestiNetwork() {
		for(String ip:destiNetworkList) {
			if(!destiNetworkMap.get(ip)) {
				//调用此方法置未发送RIP报文缓存的网络为不可达，且置下一跳路由器为该网络邻接路由器的路由表项为不可达
				setRoutringItemUnreachable(ip);
				showPanel.infoShow.append(ip+"未在限制时间内发送RIP报文信息，已置为不可达！\n");
			}
		}
	}
	
	/**
	 * (恢复网络连接时调用)
	 * 私有方法
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
	 * (模拟故障时调用)
	 * 私有方法
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
	 * 添加从网络获取的由邻接路由器发送的RIP报文集合
	 * 注：此方法由网络类对象调用，会出现并发访问this.RIPCache，需进行并发访问控制
	 * @param nRIPCache
	 */
	public synchronized void putRIPCache(Map<String,List<RoutingListItem>> nRIPCache) {
		//此处不可直接对nRIPCache使用进行RIPCache.putAll拷贝，此时拷贝的是由各个路由器发送路由表的实体项
		//D_V算法中的表项修改操作会直接修改原路由器路由表项对象导致莫名其妙的错误
		for(Map.Entry<String,List<RoutingListItem>> entry : nRIPCache.entrySet()) {
			//Key--路由器名
			String sentRouterName=entry.getKey();
			//Value--对应路由表
			List<RoutingListItem> sentRoutingList=entry.getValue();
			//复制路由表
			List<RoutingListItem> copyRoutingList=new CopyOnWriteArrayList<RoutingListItem>();
			for(RoutingListItem sitem:sentRoutingList) {
				copyRoutingList.add(new RoutingListItem(sitem));
			}
			RIPCache.put(sentRouterName,copyRoutingList);
		}
	}
	
	/**
	 * 发送获取RIP报文至邻接网络的RIPCache
	 * 注：此方法调用网络类的putRIPCache方法，将本路由器路由表封装成RIP报文发送至本
	 * 路由器邻接的网络
	 */
	private void sentSelfRIP() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		showPanel.infoShow.append(df.format(new Date())+"\n");
		//遍历直连网络列表
		for(String dNetworkID : destiNetworkList) {
			//仅向连接正常的网络发送
			if(destiNetworkMap.get(dNetworkID)) {
				//根据netID获取Network对象
				Network dNetwork=Network.NetworkList.get(dNetworkID);
				//发送本路由器routingList至网络
				//此处应该发送复制后的routingList
				//复制路由表
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
	 * RIP距离向量算法
	 * 注：需将RIPCache中所有RIP报文进行算法处理，D_V算法需独占RIPCache,routingList
	 */
	private synchronized void D_V() {
		//遍历未处理RIP报文集合
		for(Map.Entry<String,List<RoutingListItem>> entry : RIPCache.entrySet()) {
			//Key--路由器名
			String sentRouterName=entry.getKey();
			//Value--对应路由表
			List<RoutingListItem> sentRoutingList=entry.getValue();
			
			for(RoutingListItem sitem:sentRoutingList) {
				//路由表处理
				//置下一跳路由器为发送路由其
				sitem.setNextRouter(sentRouterName);
				//跳数加1
				sitem.increHopNum();
				
				//存储目的网络相同的routingListItem'Index
				int index=-1;
				
				for(int i=0;i<routingList.size();i++) {
					RoutingListItem titem=routingList.get(i);
					//判断routingList是否存在与该项目的网络相同的项，有则置index为i并跳出循环
					if(titem.equalsDNet(sitem)) {
						index=i;
						break;
					}
				}
				
				//routingList存在与该项目的网络相同的项
				if(index!=-1) {
					RoutingListItem titem=routingList.get(index);
					//判断是否有相同下一跳路由器
					if(titem.equalsNRouter(sitem)) {
						//有相同下一跳路由器
						//替换原有Item
						routingList.remove(index);
						routingList.add(sitem);
					}
					else {
						//比较跳数
						if(sitem.getHopNum()<titem.getHopNum()) {
							//跳数小于原有项
							//替换原有Item
							routingList.remove(index);
							routingList.add(sitem);
						}
					}
				}
				//不存在--添加该项目
				else {
					routingList.add(sitem);
				}
			}
		}
		//处理完成后清空RIPCache
		RIPCache.clear();
	}
	
	/**
	 * 路由表信息输出
	 * 注：输出算法需要独占routingList
	 * @param str 附加信息标识输出位置(在更新前还是后)
	 */
	public void print(String str) {
		StringBuffer sb=new StringBuffer();
		sb.append("路由器名："+routerName+"\n");
		sb.append(str+"\n");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sb.append(df.format(new Date())+"\n");
		sb.append(String.format("%15s%12s%5s\n","目的网络","下一跳路由器","跳数"));
		for(RoutingListItem i:routingList) {
			sb.append(i.toString()+"\n");
		}
		System.out.println(sb.toString());
	}
	
	/**
	 * 在面板显示信息
	 */
	private void showInfo() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		showPanel.timeLabel.setText(df.format(new Date()));
		DefaultListModel<String> lmodel=new DefaultListModel<String>();
		for(String nname:destiNetworkList) {
			lmodel.addElement(nname);
		}
		showPanel.destiNetworkList.setModel(lmodel);
		String[] title= {"目的网络","下一跳路由器","跳数"};
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
			//控制台输出测试
			//print("更新前路由表");
			showInfo();
			//发送RIP报文至网络
			sentSelfRIP();
			try {
				Thread.sleep(SleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//距离向量算法
			D_V();
			//print("更新后路由表");
		}
	}
	
}
