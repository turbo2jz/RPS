package common;

/**
 * IP地址类
 * @author always
 */
public class IPAddress {
	
	private int sub1,sub2,sub3,sub4;
	
	private String IP;
	
	private IPType type;

	/**
	 * 含参构造函数
	 * 通过IP地址字符串进行初始化，可能会出现越界
	 * @param IPstr
	 * @param t
	 */
	public IPAddress(String IPstr,IPType t) {
		String[] subArr=(IPstr.trim()).split("\\.");
		int[] subIntArr=new int[subArr.length];
		for(int i=0;i<subArr.length;i++) {
			subIntArr[i]=Integer.parseInt(subArr[i]);
		}
		sub1=subIntArr[0];
		sub2=subIntArr[1];
		sub3=subIntArr[2];
		sub4=subIntArr[3];
		IP=IPstr;
		type=t;
	}
	
	/**
	 * 含参构造函数
	 * 通过置IP地址每一位进行初始化
	 * @param s1
	 * @param s2
	 * @param s3
	 * @param s4
	 * @param t
	 */
	public IPAddress(int s1,int s2,int s3,int s4,IPType t) {
		sub1=s1;
		sub2=s2;
		sub3=s3;
		sub4=s4;
		type=t;
		IP=""+sub1+'.'+sub2+'.'+sub3+'.'+sub4;
	}
	
	/**
	 * 复制构造函数
	 */
	public IPAddress(IPAddress copyIP) {
		sub1=copyIP.sub1;
		sub2=copyIP.sub2;
		sub3=copyIP.sub3;
		sub4=copyIP.sub4;
		type=copyIP.type;
		IP=new String(copyIP.IP);
	}
	
	/**
	 * 返回IP地址类型
	 * @return
	 */
	public IPType getIpType() {
		return type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IPAddress) {
			return this.IP.equals(((IPAddress) obj).IP);
		}
		return false;
	}

	@Override
	public String toString() {
		return ""+sub1+'.'+sub2+'.'+sub3+'.'+sub4;
	}

}
