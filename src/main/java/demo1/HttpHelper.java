package demo1;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * <pre>
 * 请求工具类
 * &#64;author gzh
 *
 * </pre>
 */
public class HttpHelper {

	private static String UTF8 = "UTF-8";
	private static RequestConfig requestConfig;

	/**
	 * 
	 * <pre>
	 * @param header 头参数
	 * @param params 业务参数
	 * @param url
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public static String post(Map<String, String> header, Map<String, String> params, String url) throws Exception {
		HttpPost post = null;
		post = new HttpPost(url);
		if (header != null) {
			for (String key : header.keySet()) {
				post.addHeader(key, header.get(key));
			}
		}
		if (params != null) {
			List<BasicNameValuePair> list = new LinkedList<BasicNameValuePair>();
			post.setConfig(getRequestConfig());
			for (String key : params.keySet()) {
				list.add(new BasicNameValuePair(key, params.get(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, UTF8);
			post.setEntity(entity);
		}
		return HttpClientHelper.getHttpClient().execute(post);
	}

	/**
	 * 
	 * <pre>
	 * @param header 头参数
	 * @param jsonObject 业务参数（json格式的字符串？）
	 * @param url
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public static String post(Map<String, String> header, String jsonObject, String url) throws Exception {
		HttpPost post = null;
		post = new HttpPost(url);
		if (header != null) {
			for (String key : header.keySet()) {
				post.addHeader(key, header.get(key));
			}
		}
		if (jsonObject.isEmpty()) {
			throw new Exception("jsonObject不能为空！");
		}
		HttpEntity entity = new StringEntity(jsonObject, "UTF-8");
		post.setEntity(entity); //gzh加的。//实体类HttpEntity，有不同类型的实体。
		return HttpClientHelper.getHttpClient().execute(post);
	}

	/**
	 * 
	 * <pre>
	 * @param params 业务参数
	 * @param url
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public static String post(Map<String, String> params, String url) throws Exception {
		HttpPost post = null;
		post = new HttpPost(url);
		List<BasicNameValuePair> list = new LinkedList<BasicNameValuePair>();
		post.setConfig(getRequestConfig());
		for (String key : params.keySet()) {
			list.add(new BasicNameValuePair(key, params.get(key)));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, UTF8);
		post.setEntity(entity);
		return HttpClientHelper.getHttpClient().execute(post);
	}

	/**
	 * 
	 * <pre>
	 * 获取请求配置 
	 * @return
	 * </pre>
	 */
	public static RequestConfig getRequestConfig() {
		if (requestConfig == null) {
			requestConfig = RequestConfig.custom().setConnectionRequestTimeout(20000).setConnectTimeout(20000)
					.setSocketTimeout(20000).build();
		}
		return requestConfig;
	}

	/**
	 * 
	 * <pre>
	 * 获取ip
	 * @param request
	 * @return
	 * </pre>
	 */
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client_IP");
		}
		if (ip == null || ip.length() == 0 || "unkonwn".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.length() < 5) {
			ip = "0.0.0.0";
		}
		return ip;
	}
}