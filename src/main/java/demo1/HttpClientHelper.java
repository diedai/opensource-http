package demo1;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <pre>
 * &#64;author gzh
 *
 * </pre>
 */
public class HttpClientHelper {

	private static Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);
	private static HttpClientHelper instance = null;
	private static Lock lock = new ReentrantLock();
	private static CloseableHttpClient httpClient;

	public HttpClientHelper() {
		instance = this;
	}

	/**
	 * 
	 * <pre>
	 * 获取实例对象
	 * 
	 * 注：线程安全。
	 * @return
	 * </pre>
	 */
	public static HttpClientHelper getHttpClient() {
		if (instance == null) {
			lock.lock();
			try {
				instance = new HttpClientHelper();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
		return instance;
	}

	public void init() {
		PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager();
		pool.setMaxTotal(500);
		pool.setDefaultMaxPerRoute(50);
		httpClient = HttpClientBuilder.create().setConnectionManager(pool).build();
	}

	/**
	 * 
	 * <pre>
	 * 返回字节数组
	 * @param request
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public byte[] executeAndReturnByte(HttpRequestBase request) throws Exception {
		HttpEntity entity = null;
		CloseableHttpResponse response = null;
		byte[] base = new byte[0];
		if (request == null) {
			return base;
		}
		if (httpClient == null) {
			init();
		}
		if (httpClient == null) {
			logger.error("http获取异常");
			return base;
		}
		response = httpClient.execute(request);
		entity = response.getEntity();
		if (response.getStatusLine().getStatusCode() == 200) {
			String encode = ("" + response.getFirstHeader("Content-Encoding")).toLowerCase();
			if (encode.indexOf("gzip") > 0) {
				entity = new GzipDecompressingEntity(entity);
			}
			base = EntityUtils.toByteArray(entity);
		} else {
			logger.error("" + response.getStatusLine().getStatusCode());
		}
		EntityUtils.consumeQuietly(entity);
		response.close();
		httpClient.close();
		return base;
	}

	/**
	 * 
	 * <pre>
	 * 返回字符串
	 * @param request
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public String execute(HttpRequestBase request) throws Exception {
		byte[] base = executeAndReturnByte(request);
		if (base == null) {
			return null;
		}
		String result = new String(base, "UTF-8");
		return result;
	}
}