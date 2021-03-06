/*
 * ScanSettings.java
 * 
 * Created on August 19, 2007, 1:26 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.scan.settings;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.ConfigurationManager;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.proxy.ReverseProxyConfig;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.authentication.AuthenticationPackage;
import com.grendelscan.scan.authentication.FormBasedAuthentication;
import com.grendelscan.scan.authentication.HttpAuthenticationType;
import com.grendelscan.testing.misc.ModuleDependencyException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.MasterTestModuleCollection;
import com.grendelscan.testing.modules.impl.spidering.AutoAuthentication;
import com.grendelscan.testing.modules.settings.ConfigurationOption;
import com.grendelscan.testing.modules.settings.IntegerOption;
import com.grendelscan.testing.modules.settings.OptionGroup;
import com.grendelscan.testing.modules.settings.SelectableOption;
import com.grendelscan.testing.modules.settings.TextListOption;
import com.grendelscan.testing.modules.settings.TextOption;

/**
 * @author David Byrne
 * 
 */
public class ScanSettings implements ScanSettingsContants {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ScanSettings.class);
	// private boolean useLoggedOutDetection;
	private int acceptableAutomaticResponseCodeOverrideThreshold;
	private boolean allowAllProxyRequests = true;
	private boolean useAuthentication;
	private AuthenticationPackage authenticationPackage;
	private final Map<String, String> authenticationCredentials;
	private boolean automaticAuthentication;
	final private ArrayList<String> baseURIs;
	final private List<String> forbiddenQueryParameters;
	private boolean interceptRequests = false;
	private boolean interceptResponses = false;
	// final private List<String> irrelevantQueryParameters;
	final private List<Pattern> knownSessionIDRegexs;
	final private Map<String, Pattern> restfulPatterns;
	final private HashMap<String, Integer> manualResponseCodeOverrides;

	private int maxCategorizerThreads;
	private int maxConnectionsPerServer;
	private int maxConsecutiveFailedRequests;
	private int maxFailedRequestsPerHost;

	private int maxFileSizeKiloBytes;
	private int maxProxyThreads;
	private int maxRedirects;
	private int maxRequestDepth;
	// private int maxRequestCount;

	private int maxRequesterThreads;
	private int maxRequestRetries;
	// client limits
	private int maxRequestsPerSecond;
	private int maxTesterThreads;
	private int maxTotalConnections;

	private boolean parseHtmlDom;
	private boolean proxyEnabled;
	private String proxyIPAddress;
	private int proxyPort;

	private boolean useSocksProxy;
	private int socksPort;
	private String socksHost;

	final private List<InterceptFilter> requestInterceptFilters;
	final private List<InterceptFilter> responseInterceptFilters;
	private boolean revealHiddenFields = true;
	final private List<ReverseProxyConfig> reverseProxyConfigs;

	private String savedTextTransactionsDirectory;
	private final String scanConfigDir = DEFAULT_SCAN_CONFIG_DIR;
	private int socketReadTimeout; // in
									// seconds
	private boolean testAllDirectoriesForResponseCodeOverrides;

	private boolean testInterceptedRequests = false;
	private boolean testManualRequests;

	private boolean testProxyRequests = true;

	private String upstreamProxyAddress;

	private HttpAuthenticationType upstreamProxyAuthenticationType;
	private String upstreamProxyPassword;
	private int upstreamProxyPort;
	private String upstreamProxyUsername;
	private UrlFilters urlFilters;
	private boolean useAutomaticResponseCodeOverrides;
	private String userAgentString;
	private boolean useUpstreamProxy;
	private String logOutUri;

	public ScanSettings() {

		baseURIs = new ArrayList<String>(1);
		authenticationCredentials = new HashMap<String, String>(1);
		forbiddenQueryParameters = new ArrayList<String>(1);
		requestInterceptFilters = new ArrayList<InterceptFilter>(1);
		responseInterceptFilters = new ArrayList<InterceptFilter>(1);
		reverseProxyConfigs = new ArrayList<ReverseProxyConfig>(1);
		manualResponseCodeOverrides = new HashMap<String, Integer>(1);
		knownSessionIDRegexs = Collections
				.synchronizedList(new ArrayList<Pattern>(1));
		restfulPatterns = Collections
				.synchronizedMap(new HashMap<String, Pattern>(1));
		// irrelevantQueryParameters = new ArrayList<String>(1);
		loadDefaultSettings();
	}

	public void addBaseURI(final String uri) throws URISyntaxException,
			IllegalArgumentException {
		if (!baseURIs.contains(uri)) {
			URIStringUtils.assertAbsoluteHttpAndValid(uri);
			String fixedUri = URIStringUtils.fixBaseUri(uri);
			urlFilters.addBaseUriToPatterns(fixedUri);
			baseURIs.add(fixedUri);
			updateSettingsFile();
		} else {
			throw new IllegalArgumentException("Not a new URI ( " + uri + ")");
		}
	}

	public void addForbiddenQueryParameter(final String parameter) {
		forbiddenQueryParameters.add(parameter);
	}

	public void addKnownRestfulRegex(final String name, final Pattern pattern) {
		restfulPatterns.put(name, pattern);
	}

	public void addKnownSessionIDRegex(final Pattern pattern) {
		knownSessionIDRegexs.add(pattern);
	}

	public void addRequestInterceptFilter(final InterceptFilter filter) {
		requestInterceptFilters.add(filter);
	}

	public void addResponseInterceptFilter(final InterceptFilter filter) {
		responseInterceptFilters.add(filter);
	}

	public void addReverseProxyConfig(final ReverseProxyConfig config) {
		reverseProxyConfigs.add(config);
		updateSettingsFile();
	}

	public void clearBaseURIs() {
		List<String> tmpUris = new ArrayList<String>(baseURIs);
		for (String uri : tmpUris) {
			removeBaseURI(uri);
		}
		updateSettingsFile();
	}

	public void clearForbiddenQueryParameters() {
		forbiddenQueryParameters.clear();
	}

	public void clearKnownRestfulRegexs() {
		restfulPatterns.clear();
	}

	public void clearKnownSessionIDRegexs() {
		knownSessionIDRegexs.clear();
	}

	public void clearRequestInterceptFilters() {
		requestInterceptFilters.clear();
	}

	public void clearResponseInterceptFilters() {
		responseInterceptFilters.clear();
	}

	public void clearReverseProxyConfig() {
		reverseProxyConfigs.clear();
		updateSettingsFile();
	}

	public void convertBaseUris2WhiteLists() {
		for (String baseURI : baseURIs) {
			try {
				urlFilters.addBaseUriToPatterns(baseURI);
			} catch (URISyntaxException e) {
				IllegalStateException ise = new IllegalStateException(
						"Really, really weird problem with uri parsing", e);
				LOGGER.error(e.toString(), e);
				throw ise;
			}
		}
	}

	public String generateDefaultOutputDirectory() {
		Date date = new Date();
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
		s.format(date);
		return "scans" + File.separator + "grendel-scan-" + s.format(date);
	}

	public int getAcceptableAutomaticResponseCodeOverrideThreshold() {
		return acceptableAutomaticResponseCodeOverrideThreshold;
	}

	public final Map<String, String> getAuthenticationCredentials() {
		return authenticationCredentials;
	}

	public AuthenticationPackage getAuthenticationPackage() {
		return authenticationPackage;
	}

	public final String getLogOutUri() {
		return logOutUri;
	}

	public int getMaxCategorizerThreads() {
		return maxCategorizerThreads;
	}

	// /**
	// * Irrelevent parameters shouldn't be viewed as significant to application
	// * logic
	// * (e.g. sessionIDs), but can still be tested for things like XSS, or even
	// * SQL injection.
	// *
	// * @return
	// */
	// public List<String> getReadOnlyIrrelevantQueryParameters()
	// {
	// return new ArrayList<String> (irrelevantQueryParameters);
	// }

	public int getMaxConnectionsPerServer() {
		return maxConnectionsPerServer;
	}

	public int getMaxConsecutiveFailedRequests() {
		return maxConsecutiveFailedRequests;
	}

	public int getMaxFailedRequestsPerHost() {
		return maxFailedRequestsPerHost;
	}

	public int getMaxFileSizeKiloBytes() {
		return maxFileSizeKiloBytes;
	}

	public int getMaxProxyThreads() {
		return maxProxyThreads;
	}

	public int getMaxRedirects() {
		return maxRedirects;
	}

	public int getMaxRequestDepth() {
		return maxRequestDepth;
	}

	public int getMaxRequesterThreads() {
		return maxRequesterThreads;
	}

	// public void clearIrrelevantQueryParameters()
	// {
	// irrelevantQueryParameters.clear();
	// }
	//
	// public void addIrrelevantQueryParameter(String parameter)
	// {
	// irrelevantQueryParameters.add(parameter);
	// }
	//
	// public void removeIrrelevantQueryParameter(String parameter)
	// {
	// irrelevantQueryParameters.remove(parameter);
	// }

	public int getMaxRequestRetries() {
		return maxRequestRetries;
	}

	public int getMaxRequestsPerSecond() {
		return maxRequestsPerSecond;
	}

	public int getMaxTesterThreads() {
		return maxTesterThreads;
	}

	public int getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public String getProxyIPAddress() {
		return proxyIPAddress;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public Map<String, String> getReadOnlyAuthenticationCredentials() {
		return new HashMap<String, String>(authenticationCredentials);
	}

	/**
	 * @return the baseURIs
	 */
	public ArrayList<String> getReadOnlyBaseURIs() {
		return new ArrayList<String>(baseURIs);
	}

	/**
	 * Forbidden parameters are considered dangerous and should not be tested at
	 * all. They may be used for crawling, but only if the value is supplied by
	 * the server (i.e. a hidden field, or part of a URL).
	 * 
	 * @return
	 */
	public List<String> getReadOnlyForbiddenQueryParameters() {
		return new ArrayList<String>(forbiddenQueryParameters);
	}

	public List<Pattern> getReadOnlyKnownSessionIDRegexs() {
		return new ArrayList<Pattern>(knownSessionIDRegexs);
	}

	public Map<String, Integer> getReadOnlyManualResponseCodeOverrides() {
		return new HashMap<String, Integer>(manualResponseCodeOverrides);
	}

	public List<InterceptFilter> getReadOnlyRequestInterceptFilters() {
		return new ArrayList<InterceptFilter>(requestInterceptFilters);
	}

	public List<InterceptFilter> getReadOnlyResponseInterceptFilters() {
		return new ArrayList<InterceptFilter>(responseInterceptFilters);
	}

	public Map<String, Pattern> getReadOnlyRestfulRegexs() {
		return new HashMap<String, Pattern>(restfulPatterns);
	}

	// public String getOutputDirectory()
	// {
	// return outputDirectory;
	// }

	public List<ReverseProxyConfig> getReadOnlyReverseProxyConfigs() {
		return new ArrayList<ReverseProxyConfig>(reverseProxyConfigs);
	}

	public final Map<String, Pattern> getRestfulPatterns() {
		return restfulPatterns;
	}

	public String getSavedTextTransactionsDirectory() {
		return savedTextTransactionsDirectory;
	}

	public String getScanConfigDir() {
		return scanConfigDir;
	}

	public int getSocketReadTimeout() {
		return socketReadTimeout;
	}

	public final String getSocksHost() {
		return socksHost;
	}

	public final int getSocksPort() {
		return socksPort;
	}

	public boolean getTestAllDirectoriesForResponseCodeOverrides() {
		return testAllDirectoriesForResponseCodeOverrides;
	}

	public String getUpstreamProxyAddress() {
		return upstreamProxyAddress;
	}

	public HttpAuthenticationType getUpstreamProxyAuthenticationType() {
		return upstreamProxyAuthenticationType;
	}

	public String getUpstreamProxyPassword() {
		return upstreamProxyPassword;
	}

	public int getUpstreamProxyPort() {
		return upstreamProxyPort;
	}

	public String getUpstreamProxyUsername() {
		return upstreamProxyUsername;
	}

	public UrlFilters getUrlFilters() {
		return urlFilters;
	}

	public boolean getUseAutomaticResponseCodeOverrides() {
		return useAutomaticResponseCodeOverrides;
	}

	/**
	 * @return the userAgentString
	 */
	public String getUserAgentString() {
		return userAgentString;
	}

	public boolean getUseUpstreamProxy() {
		return useUpstreamProxy;
	}

	public boolean isAllowAllProxyRequests() {
		return allowAllProxyRequests;
	}

	public final boolean isAutomaticAuthentication() {
		return automaticAuthentication;
	}

	public boolean isInterceptRequests() {
		return interceptRequests;
	}

	// public boolean isUseLoggedOutDetection()
	// {
	// return useLoggedOutDetection;
	// }
	//
	// public void setUseLoggedOutDetection(boolean useLoggedOutDetection)
	// {
	// this.useLoggedOutDetection = useLoggedOutDetection;
	// }

	public boolean isInterceptResponses() {
		return interceptResponses;
	}

	public boolean isParseHtmlDom() {
		return parseHtmlDom;
	}

	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

	public boolean isRevealHiddenFields() {
		return revealHiddenFields;
	}

	public boolean isTestInterceptedRequests() {
		return testInterceptedRequests;
	}

	public boolean isTestManualRequests() {
		return testManualRequests;
	}

	public boolean isTestProxyRequests() {
		return testProxyRequests;
	}

	public boolean isUseAuthentication() {
		return useAuthentication;
	}

	public final boolean isUseSocksProxy() {
		return useSocksProxy;
	}

	private Map<String, String> loadAllOptionValues(
			final XMLConfiguration config, final int moduleIndex) {
		HashMap<String, String> options = new HashMap<String, String>(1);
		int optionIndex = 0;
		while (true) {
			String name = config.getString(TEST_MODULES + moduleIndex
					+ TEST_MODULES__OPTIONS + optionIndex
					+ TEST_MODULES__OPTION_NAME, null);
			if (name != null) {
				String value = config.getString(TEST_MODULES + moduleIndex
						+ TEST_MODULES__OPTIONS + optionIndex
						+ TEST_MODULES__OPTION_VALUE);
				optionIndex++;
				if (value != null) {
					options.put(name, value);
				} else {
					LOGGER.info("Config value for '" + name + "' is null.");
				}
			} else {
				break;
			}
		}
		return options;
	}

	public void loadDefaultSettings() {
		String filename = GrendelScan.defaultConfigDirectory + File.separator
				+ scanConfigDir + File.separator
				+ ConfigurationManager.getString("default_scan_config");
		XMLConfiguration config;
		try {
			config = new XMLConfiguration(filename);
		} catch (ConfigurationException e) {
			config = new XMLConfiguration();
		}

		loadScanSettings(config);
	}

	private void loadReverseProxySettings(final XMLConfiguration config) {
		int index = 0;
		reverseProxyConfigs.clear();
		try {
			while (true) {
				ReverseProxyConfig proxyConfig = new ReverseProxyConfig();
				proxyConfig.setBindIP(config.getString(REVERSE_PROXIES + index
						+ REVERSE_PROXIES__LOCAL_IP));
				proxyConfig.setBindPort(config.getInt(REVERSE_PROXIES + index
						+ REVERSE_PROXIES__LOCAL_PORT));
				proxyConfig.setRemoteHost(config.getString(REVERSE_PROXIES
						+ index + REVERSE_PROXIES__REMOTE_HOST));
				proxyConfig.setRemotePort(config.getInt(REVERSE_PROXIES + index
						+ REVERSE_PROXIES__REMOTE_PORT));
				proxyConfig.setWebHostname(config.getString(REVERSE_PROXIES
						+ index + REVERSE_PROXIES__WEB_HOSTNAME));
				proxyConfig.setSsl(config.getBoolean(REVERSE_PROXIES + index
						+ REVERSE_PROXIES__SSL));
				reverseProxyConfigs.add(proxyConfig);
				index++;
			}
		} catch (NoSuchElementException e) {
			// Done
		}
	}

	public void loadScanSettings(final String filename)
			throws ConfigurationException {
		XMLConfiguration config = new XMLConfiguration(filename);
		loadScanSettings(config);
	}

	// public int getMaxRequestCount()
	// {
	// return maxRequestCount;
	// }
	//
	// public void setMaxRequestCount(int maxRequestCount)
	// {
	// this.maxRequestCount = maxRequestCount;
	// }

	private void loadScanSettings(final XMLConfiguration config) {
		maxRedirects = config.getInt(HTTP_CLIENT__MAX_REDIRECTS, 3);
		// useLoggedOutDetection =
		// config.getBoolean(SESSION_TRACKING__USE_LOGGED_OUT_DETECTION, false);
		acceptableAutomaticResponseCodeOverrideThreshold = config.getInt(
				SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__THRESHOLD, 80);
		useAuthentication = config.getBoolean(
				AUTHENTICATION__USE_AUTHENTICATION, false);
		String authType = config.getString(AUTHENTICATION__TYPE, null);
		authenticationPackage = null;
		if (authType != null) {
			if (authType.equalsIgnoreCase(AUTHENTICATION__TYPE__FORM)) {
				String rawUri = config.getString(AUTHENTICATION__URI);
				logOutUri = config.getString(AUTHENTICATION__LOGOUT_URI, "");
				String method = config.getString(AUTHENTICATION__METHOD, "GET");
				String postQuery = config.getString(AUTHENTICATION__POST_QUERY,
						"");
				String userParam = config.getString(
						AUTHENTICATION__USER_PARAMETER, "");
				String passwordParam = config.getString(
						AUTHENTICATION__PASSWORD_PARAMETER, "");
				authenticationPackage = new FormBasedAuthentication(rawUri,
						method, postQuery, userParam, passwordParam);
			}
			// else if (authType.equalsIgnoreCase(AUTHENTICATION__TYPE__HTTP))
			// {
			// authenticationPackage = new HttpBasedAuthentication();
			// }
		}

		{
			int index = 0;
			while (true) {
				String username = config.getString(AUTHENTICATION__CREDENTIALS
						+ index + AUTHENTICATION__CREDENTIALS_USERNAME, null);
				String password = config.getString(AUTHENTICATION__CREDENTIALS
						+ index + AUTHENTICATION__CREDENTIALS_PASSWORD, "");
				index++;
				if (username != null) {
					authenticationCredentials.put(username, password);
				} else {
					break;
				}
			}
		}
		//
		// {
		// irrelevantQueryParameters.clear();
		// int index = 0;
		// while (true)
		// {
		// String param =
		// config.getString(SCAN_SETTINGS__RESTRICTIONS__IRRELEVANT_PARAMETERS +
		// index++ + ")", null);
		// if (param != null)
		// {
		// irrelevantQueryParameters.add(param);
		// }
		// else
		// {
		// break;
		// }
		// }
		// }

		{
			forbiddenQueryParameters.clear();
			int index = 0;
			while (true) {
				String param = config.getString(
						SCAN_SETTINGS__RESTRICTIONS__FORBIDDEN_PARAMETERS
								+ index++ + ")", null);
				if (param != null) {
					forbiddenQueryParameters.add(param);
				} else {
					break;
				}
			}
		}

		{
			baseURIs.clear();
			int index = 0;
			while (true) {
				String param = config.getString(SCAN_SETTINGS__BASE_URIS
						+ index++ + ")", null);
				if (param != null) {
					baseURIs.add(URIStringUtils.fixBaseUri(param));
				} else {
					break;
				}
			}
		}

		{

			manualResponseCodeOverrides.clear();
			int index = 0;
			while (true) {
				String pattern = config
						.getString(
								SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__BASE
										+ index
										+ SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__PATTERN,
								null);
				int code = config
						.getInt(SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__BASE
								+ index
								+ SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__STATUS_CODE,
								0);
				index++;
				if (pattern != null) {
					manualResponseCodeOverrides.put(pattern, code);
				} else {
					break;
				}
			}
		}

		maxConnectionsPerServer = config.getInt(
				HTTP_CLIENT__MAX_CONNECTIONS_PER_SERVER, 5);
		maxConsecutiveFailedRequests = config.getInt(
				HTTP_CLIENT__MAX_CONSECUTIVE_FAILED_REQUESTS, 10);
		maxFailedRequestsPerHost = config.getInt(
				HTTP_CLIENT__MAX_FAILED_REQUESTS_PER_SERVER, 500);
		maxFileSizeKiloBytes = (int) Math.round((double) config.getInt(
				HTTP_CLIENT__MAX_FILE_SIZE, 1048576) / (double) 1024);
		maxRequestsPerSecond = config.getInt(
				HTTP_CLIENT__MAX_REQUESTS_PER_SECOND, 30);
		// maxRequestCount = config.getInt(HTTP_CLIENT__MAX_REQUEST_COUNT, 0);
		maxRequestDepth = config.getInt(HTTP_CLIENT__MAX_REQUEST_DEPTH, 10);
		maxRequestRetries = config.getInt(HTTP_CLIENT__MAX_REQUEST_RETRIES, 3);
		maxTotalConnections = config.getInt(HTTP_CLIENT__MAX_TOTAL_CONNECTIONS,
				10);
		socketReadTimeout = config.getInt(HTTP_CLIENT__SOCKET_READ_TIMEOUT, 60);
		upstreamProxyAddress = config.getString(
				HTTP_CLIENT__UPSTREAM_PROXY__ADDRESS, "");
		upstreamProxyPassword = config.getString(
				HTTP_CLIENT__UPSTREAM_PROXY__PASSWORD, "");
		upstreamProxyPort = config.getInt(HTTP_CLIENT__UPSTREAM_PROXY__PORT,
				8080);
		upstreamProxyUsername = config.getString(
				HTTP_CLIENT__UPSTREAM_PROXY__USERNAME, "");
		useUpstreamProxy = config.getBoolean(
				HTTP_CLIENT__UPSTREAM_PROXY__USE_UPSTREAM_PROXY, false);
		socksPort = config.getInteger(HTTP_CLIENT__SOCKS_PROXY__SOCKS_PORT,
				1080);
		socksHost = config.getString(HTTP_CLIENT__SOCKS_PROXY__SOCKS_HOST,
				"localhost");
		setUseSocksProxy(config.getBoolean(
				HTTP_CLIENT__SOCKS_PROXY__USE_SOCKS_PROXY, false)); // must
																	// use
																	// setter
																	// and
																	// come
																	// after
																	// host
		useSocksProxy = config.getBoolean(
				HTTP_CLIENT__SOCKS_PROXY__USE_SOCKS_PROXY, false);
		userAgentString = config.getString(HTTP_CLIENT__USER_AGENT_STRING, "");
		allowAllProxyRequests = config.getBoolean(
				PROXY_SETTINGS__ALLOW_ALL_PROXY_REQUESTS, true);
		interceptRequests = config.getBoolean(
				PROXY_SETTINGS__INTERCEPT_REQUESTS, false);
		interceptResponses = config.getBoolean(
				PROXY_SETTINGS__INTERCEPT_RESPONSES, false);
		maxProxyThreads = config.getInt(PROXY_SETTINGS__MAX_PROXY_THREADS, 2);
		proxyIPAddress = config.getString(PROXY_SETTINGS__PROXY_BIND_ADDRESS,
				"127.0.0.1");
		proxyPort = config.getInt(PROXY_SETTINGS__PROXY_BIND_PORT, 8008);
		proxyEnabled = config.getBoolean(PROXY_SETTINGS__PROXY_ENABLED, true);
		revealHiddenFields = config.getBoolean(
				PROXY_SETTINGS__REVEAL_HIDDEN_FIELDS, true);
		testInterceptedRequests = config.getBoolean(
				PROXY_SETTINGS__TEST_INTERCEPTED_REQUESTS, true);
		testProxyRequests = config.getBoolean(
				PROXY_SETTINGS__TEST_PROXY_REQUESTS, true);

		maxCategorizerThreads = config.getInt(
				SCAN_SETTINGS__QUEUES__MAX_CATEGORIZER_THREADS, 5);
		maxRequesterThreads = config.getInt(
				SCAN_SETTINGS__QUEUES__MAX_REQUESTER_THREADS, 10);
		maxTesterThreads = config.getInt(
				SCAN_SETTINGS__QUEUES__MAX_TESTER_THREADS, 15);
		testAllDirectoriesForResponseCodeOverrides = config.getBoolean(
				SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__TEST_ALL_DIRECTORIES,
				true);
		useAutomaticResponseCodeOverrides = config
				.getBoolean(
						SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__USE_AUTOMATIC_OVERRIDES,
						true);
		parseHtmlDom = config.getBoolean(
				SCAN_SETTINGS__COMPAIRISONS__PARSE_HTML_DOM, true);

		// outputDirectory =
		// config.getString(STORAGE_SETTINGS__OUTPUT_DIRECTORY, "");
		savedTextTransactionsDirectory = config.getString(
				STORAGE_SETTINGS__TRANSACTION_DIRECTORY, "http-transactions");

		urlFilters = new UrlFilters();
		{
			int index = 0;
			while (true) {
				String pattern = config.getString(
						SCAN_SETTINGS__RESTRICTIONS__URL_BLACKLIST + index++
								+ ")", null);
				if (pattern != null) {
					urlFilters.addLoadedUrlBlacklist(pattern);
				} else {
					break;
				}
			}
		}
		{
			int index = 0;
			while (true) {
				String pattern = config.getString(
						SCAN_SETTINGS__RESTRICTIONS__URL_WHITELIST + index++
								+ ")", null);
				if (pattern != null) {
					urlFilters.addLoadedUrlWhitelist(pattern);
				} else {
					break;
				}
			}
		}

		{
			restfulPatterns.clear();
			int index = 0;
			while (true) {
				String name = config.getString(
						SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__BASE + index++
								+ SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__NAME,
						null);
				String pattern = config
						.getString(
								SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__BASE
										+ index
										+ SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__PATTERN,
								null);
				if (pattern != null) {
					try {
						restfulPatterns.put(name, Pattern.compile(pattern));
					} catch (Exception e) {
						LOGGER.error(
								"Error in restful query pattern from config file: "
										+ e.toString(), e);
					}
				} else {
					break;
				}
			}
		}

		{
			knownSessionIDRegexs.clear();
			int index = 0;
			while (true) {
				String pattern = config.getString(
						SESSION_TRACKING__KNOWN_SESSION_ID_PATTERNS + index++
								+ ")", null);
				if (pattern != null) {
					try {
						knownSessionIDRegexs.add(Pattern.compile(pattern,
								Pattern.CASE_INSENSITIVE));
					} catch (Exception e) {
						LOGGER.error(
								"Error in session ID pattern from config file: "
										+ e.toString(), e);
					}
				} else {
					break;
				}
			}
		}

		requestInterceptFilters.clear();
		responseInterceptFilters.clear();
		reverseProxyConfigs.clear();

		loadTestModuleSettings(config);
		loadReverseProxySettings(config);

		// keep at end
		setAutomaticAuthentication(config.getBoolean(
				AUTHENTICATION__AUTOMATIC_AUTHENTICATION, true));

	}

	private void loadTestModuleSettings(final XMLConfiguration config) {
		int index = 0;
		while (true) {
			String moduleClassName = config.getString(TEST_MODULES + index
					+ TEST_MODULES__MODULE_CLASS, "");
			if (moduleClassName.isEmpty()) {
				break;
			}
			Class<? extends AbstractTestModule> moduleClass;
			try {
				moduleClass = (Class<? extends AbstractTestModule>) Class
						.forName(moduleClassName);
				if (config.getBoolean(TEST_MODULES + index
						+ TEST_MODULES__ENABLED)) {
					Scan.getInstance().enableTestModule(moduleClass);
				} else {
					try {
						Scan.getInstance().disableTestModule(moduleClass);
					} catch (ModuleDependencyException e) {
						LOGGER.error(
								"Problem with configuration of modules due to depenencies: "
										+ e.toString(), e);
					}
				}
				AbstractTestModule module = MasterTestModuleCollection
						.getInstance().getTestModule(moduleClass);
				if (module == null) {
					LOGGER.warn("Config found for module number " + moduleClass
							+ " which doesn't seem to exist.");
				} else {
					Map<String, String> options = loadAllOptionValues(config,
							index);

					for (ConfigurationOption option : module
							.getConfigurationOptions()) {
						populateOptionSettings(option, options);
					}
				}
			} catch (ClassNotFoundException e1) {
				LOGGER.warn("Invalid test module class (" + moduleClassName
						+ "): " + e1.toString());
			} catch (ClassCastException e1) {
				LOGGER.warn("Invalid test module class (" + moduleClassName
						+ "): " + e1.toString());
			}
			index++;
		}
	}

	private void populateOptionSettings(final ConfigurationOption option,
			final Map<String, String> options) {
		if (option instanceof OptionGroup) {
			for (ConfigurationOption subOption : ((OptionGroup) option)
					.getAllOptions()) {
				populateOptionSettings(subOption, options);
			}
		} else {
			String name = option.getName();
			if (options.containsKey(name)) {
				Object value = options.get(name);
				if (option instanceof TextOption) {
					((TextOption) option).setValue((String) value);
				} else if (option instanceof IntegerOption) {
					((IntegerOption) option).setValue(Integer
							.valueOf((String) value));
				} else if (option instanceof SelectableOption) {
					((SelectableOption) option).setSelected(Boolean
							.valueOf((String) value));
				} else if (option instanceof TextListOption) {
					TextListOption listOption = (TextListOption) option;
					for (String item : (String[]) value) {
						listOption.add(item);
					}
				} else {
					LOGGER.error(
							"Very odd problem with loading module options. This probably needs to be updated.",
							new Throwable());
				}
			} else {
				LOGGER.debug("Config option '" + name
						+ "' not found in config file; using default.");
			}
		}
	}

	public void removeBaseURI(final String uri) {
		if (baseURIs.contains(uri)) {
			urlFilters.removeBaseUriToPatterns(uri);
			baseURIs.remove(uri);
			updateSettingsFile();
		}
	}

	public void removeForbiddenQueryParameter(final String parameter) {
		forbiddenQueryParameters.remove(parameter);
	}

	public void removeKnownRestfulRegex(final String name) {
		restfulPatterns.remove(name);
	}

	public void removeKnownSessionIDRegex(final Pattern pattern) {
		knownSessionIDRegexs.remove(pattern);
	}

	public void removeRequestInterceptFilter(final InterceptFilter filter) {
		requestInterceptFilters.remove(filter);
	}

	public void removeResponseInterceptFilter(final InterceptFilter filter) {
		responseInterceptFilters.remove(filter);
	}

	public void removeReverseProxyConfig(final ReverseProxyConfig config) {
		reverseProxyConfigs.remove(config);
		updateSettingsFile();
	}

	private void saveReverseProxySettings(final XMLConfiguration config) {
		int index = 0;
		for (ReverseProxyConfig reverseProxyConfig : reverseProxyConfigs) {
			config.addProperty(REVERSE_PROXIES + index
					+ REVERSE_PROXIES__LOCAL_IP, reverseProxyConfig.getBindIP());
			config.addProperty(REVERSE_PROXIES + index
					+ REVERSE_PROXIES__LOCAL_PORT,
					reverseProxyConfig.getBindPort());
			config.addProperty(REVERSE_PROXIES + index
					+ REVERSE_PROXIES__REMOTE_HOST,
					reverseProxyConfig.getRemoteHost());
			config.addProperty(REVERSE_PROXIES + index
					+ REVERSE_PROXIES__REMOTE_PORT,
					reverseProxyConfig.getRemotePort());
			config.addProperty(REVERSE_PROXIES + index
					+ REVERSE_PROXIES__WEB_HOSTNAME,
					reverseProxyConfig.getWebHostname());
			config.addProperty(REVERSE_PROXIES + index + REVERSE_PROXIES__SSL,
					reverseProxyConfig.isSsl());
			index++;
		}
	}

	public void saveScanSettings(final String filename)
			throws ConfigurationException {
		XMLConfiguration config = new XMLConfiguration();
		config.setProperty(HTTP_CLIENT__MAX_REDIRECTS, maxRedirects);
		// config.setProperty(SESSION_TRACKING__USE_LOGGED_OUT_DETECTION,
		// useLoggedOutDetection);
		config.setProperty(SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__THRESHOLD,
				acceptableAutomaticResponseCodeOverrideThreshold);

		if (authenticationPackage != null) {
			if (authenticationPackage instanceof FormBasedAuthentication) {
				config.setProperty(AUTHENTICATION__TYPE,
						AUTHENTICATION__TYPE__FORM);
				FormBasedAuthentication auth = (FormBasedAuthentication) authenticationPackage;
				config.setProperty(AUTHENTICATION__USER_PARAMETER,
						auth.getUserParameterName());
				config.setProperty(AUTHENTICATION__PASSWORD_PARAMETER,
						auth.getPasswordParameterName());
				config.setProperty(AUTHENTICATION__POST_QUERY,
						auth.getPostQuery());
				config.setProperty(AUTHENTICATION__URI, auth.getUri());
				config.setProperty(AUTHENTICATION__LOGOUT_URI, logOutUri);
				config.setProperty(AUTHENTICATION__METHOD, auth.getMethod());
			} else {
				config.setProperty(AUTHENTICATION__TYPE,
						AUTHENTICATION__TYPE__HTTP);
			}

		}

		config.setProperty(AUTHENTICATION__USE_AUTHENTICATION,
				useAuthentication);
		config.setProperty(AUTHENTICATION__AUTOMATIC_AUTHENTICATION,
				automaticAuthentication);

		{
			int index = 0;
			for (String username : authenticationCredentials.keySet()) {
				config.addProperty(AUTHENTICATION__CREDENTIALS + index
						+ AUTHENTICATION__CREDENTIALS_USERNAME, username);
				config.addProperty(AUTHENTICATION__CREDENTIALS + index
						+ AUTHENTICATION__CREDENTIALS_PASSWORD,
						authenticationCredentials.get(username));
				index++;
			}
		}

		{
			int index = 0;
			for (String baseUri : baseURIs) {
				config.addProperty(SCAN_SETTINGS__BASE_URIS + index++ + ")",
						baseUri);
			}
		}

		// {
		// int index = 0;
		// for (String param : irrelevantQueryParameters)
		// {
		// config.addProperty(SCAN_SETTINGS__RESTRICTIONS__IRRELEVANT_PARAMETERS
		// + index++ + ")", param);
		// }
		// }
		{
			int index = 0;
			for (String param : forbiddenQueryParameters) {
				config.addProperty(
						SCAN_SETTINGS__RESTRICTIONS__FORBIDDEN_PARAMETERS
								+ index++ + ")", param);
			}
		}
		{
			int index = 0;
			for (String pattern : manualResponseCodeOverrides.keySet()) {
				config.addProperty(SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__BASE
						+ index
						+ SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__PATTERN,
						pattern);
				config.addProperty(SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__BASE
						+ index
						+ SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__STATUS_CODE,
						manualResponseCodeOverrides.get(pattern));
				index++;
			}
		}

		config.setProperty(HTTP_CLIENT__MAX_CONNECTIONS_PER_SERVER,
				maxConnectionsPerServer);
		config.setProperty(HTTP_CLIENT__MAX_CONSECUTIVE_FAILED_REQUESTS,
				maxConsecutiveFailedRequests);
		config.setProperty(HTTP_CLIENT__MAX_FAILED_REQUESTS_PER_SERVER,
				maxFailedRequestsPerHost);
		config.setProperty(HTTP_CLIENT__MAX_FILE_SIZE, maxFileSizeKiloBytes);
		config.setProperty(HTTP_CLIENT__MAX_REQUESTS_PER_SECOND,
				maxRequestsPerSecond);
		// config.setProperty(HTTP_CLIENT__MAX_REQUEST_COUNT, maxRequestCount);
		config.setProperty(HTTP_CLIENT__MAX_REQUEST_DEPTH, maxRequestDepth);
		config.setProperty(HTTP_CLIENT__MAX_REQUEST_RETRIES, maxRequestRetries);
		config.setProperty(HTTP_CLIENT__MAX_TOTAL_CONNECTIONS,
				maxTotalConnections);
		config.setProperty(HTTP_CLIENT__SOCKET_READ_TIMEOUT, socketReadTimeout);
		config.setProperty(HTTP_CLIENT__UPSTREAM_PROXY__ADDRESS,
				upstreamProxyAddress);
		config.setProperty(HTTP_CLIENT__UPSTREAM_PROXY__PASSWORD,
				upstreamProxyPassword);
		config.setProperty(HTTP_CLIENT__UPSTREAM_PROXY__PORT, upstreamProxyPort);
		config.setProperty(HTTP_CLIENT__UPSTREAM_PROXY__USERNAME,
				upstreamProxyUsername);
		config.setProperty(HTTP_CLIENT__UPSTREAM_PROXY__USE_UPSTREAM_PROXY,
				useUpstreamProxy);
		config.setProperty(HTTP_CLIENT__SOCKS_PROXY__USE_SOCKS_PROXY,
				useSocksProxy);
		config.setProperty(HTTP_CLIENT__SOCKS_PROXY__SOCKS_PORT, socksPort);
		config.setProperty(HTTP_CLIENT__SOCKS_PROXY__SOCKS_HOST, socksHost);
		config.setProperty(HTTP_CLIENT__USER_AGENT_STRING, userAgentString);
		config.setProperty(PROXY_SETTINGS__ALLOW_ALL_PROXY_REQUESTS,
				allowAllProxyRequests);
		config.setProperty(PROXY_SETTINGS__INTERCEPT_REQUESTS,
				interceptRequests);
		config.setProperty(PROXY_SETTINGS__INTERCEPT_RESPONSES,
				interceptResponses);
		config.setProperty(PROXY_SETTINGS__MAX_PROXY_THREADS, maxProxyThreads);
		config.setProperty(PROXY_SETTINGS__PROXY_BIND_ADDRESS, proxyIPAddress);
		config.setProperty(PROXY_SETTINGS__PROXY_BIND_PORT, proxyPort);
		config.setProperty(PROXY_SETTINGS__PROXY_ENABLED, proxyEnabled);
		config.setProperty(PROXY_SETTINGS__REVEAL_HIDDEN_FIELDS,
				revealHiddenFields);
		config.setProperty(PROXY_SETTINGS__TEST_INTERCEPTED_REQUESTS,
				testInterceptedRequests);
		config.setProperty(PROXY_SETTINGS__TEST_PROXY_REQUESTS,
				testProxyRequests);
		config.setProperty(SCAN_SETTINGS__QUEUES__MAX_CATEGORIZER_THREADS,
				maxCategorizerThreads);
		config.setProperty(SCAN_SETTINGS__QUEUES__MAX_REQUESTER_THREADS,
				maxRequesterThreads);
		config.setProperty(SCAN_SETTINGS__QUEUES__MAX_TESTER_THREADS,
				maxTesterThreads);
		config.setProperty(
				SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__TEST_ALL_DIRECTORIES,
				testAllDirectoriesForResponseCodeOverrides);
		config.setProperty(
				SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__USE_AUTOMATIC_OVERRIDES,
				useAutomaticResponseCodeOverrides);
		config.setProperty(SCAN_SETTINGS__COMPAIRISONS__PARSE_HTML_DOM,
				parseHtmlDom);

		// config.setProperty(STORAGE_SETTINGS__OUTPUT_DIRECTORY,
		// outputDirectory);
		config.setProperty(STORAGE_SETTINGS__TRANSACTION_DIRECTORY,
				savedTextTransactionsDirectory);

		{
			int index = 0;
			for (String filter : urlFilters.getBlacklistsAsString()) {
				config.addProperty(SCAN_SETTINGS__RESTRICTIONS__URL_BLACKLIST
						+ index++ + ")", filter);
			}
		}
		{
			int index = 0;
			for (String filter : urlFilters.getWhitelistsAsString()) {
				config.addProperty(SCAN_SETTINGS__RESTRICTIONS__URL_WHITELIST
						+ index++ + ")", filter);
			}
		}
		{
			int index = 0;
			for (String name : restfulPatterns.keySet()) {
				config.addProperty(
						SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__BASE + index++
								+ SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__NAME,
						name);
				config.addProperty(SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__BASE
						+ index
						+ SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__PATTERN,
						restfulPatterns.get(name).pattern());
			}
		}

		{
			int index = 0;
			for (Pattern pattern : knownSessionIDRegexs) {
				config.addProperty(SESSION_TRACKING__KNOWN_SESSION_ID_PATTERNS
						+ index++ + ")", pattern.pattern());
			}
		}

		saveTestModuleSettings(config);
		saveReverseProxySettings(config);

		config.save(filename);
	}

	private int saveTestModuleOption(final XMLConfiguration config,
			final int moduleIndex, final int lastOptionIndex,
			final ConfigurationOption option) {
		int optionIndex = lastOptionIndex;
		if (option instanceof OptionGroup) {
			for (ConfigurationOption subOption : ((OptionGroup) option)
					.getAllOptions()) {
				optionIndex = saveTestModuleOption(config, moduleIndex,
						optionIndex, subOption);
			}
			return optionIndex;
		}

		config.addProperty(TEST_MODULES + moduleIndex + TEST_MODULES__OPTIONS
				+ optionIndex + TEST_MODULES__OPTION_NAME, option.getName());
		if (option instanceof TextOption) {
			config.addProperty(TEST_MODULES + moduleIndex
					+ TEST_MODULES__OPTIONS + optionIndex
					+ TEST_MODULES__OPTION_VALUE,
					((TextOption) option).getValue());
			return optionIndex + 1;
		} else if (option instanceof IntegerOption) {
			config.addProperty(TEST_MODULES + moduleIndex
					+ TEST_MODULES__OPTIONS + optionIndex
					+ TEST_MODULES__OPTION_VALUE,
					((IntegerOption) option).getValue());
			return optionIndex + 1;
		} else if (option instanceof SelectableOption) {
			config.addProperty(TEST_MODULES + moduleIndex
					+ TEST_MODULES__OPTIONS + optionIndex
					+ TEST_MODULES__OPTION_VALUE,
					((SelectableOption) option).isSelected());
			return optionIndex + 1;
		} else if (option instanceof TextListOption) {
			TextListOption listOption = (TextListOption) option;
			for (String item : listOption.getReadOnlyData()) {
				config.addProperty(TEST_MODULES + moduleIndex
						+ TEST_MODULES__OPTIONS + optionIndex
						+ TEST_MODULES__OPTION_VALUE, item);
			}
			return optionIndex + 1;
		} else {
			LOGGER.error(
					"Very odd problem with saving module options. This probably needs to be updated.",
					new Throwable());
			return optionIndex;
		}
	}

	private void saveTestModuleSettings(final XMLConfiguration config) {
		int index = 0;
		for (AbstractTestModule module : MasterTestModuleCollection
				.getInstance().getAllTestModules()) {
			config.addProperty(TEST_MODULES + index
					+ TEST_MODULES__MODULE_CLASS, module.getClass().getName());
			config.addProperty(
					TEST_MODULES + index + TEST_MODULES__MODULE_NAME,
					module.getName());
			config.addProperty(TEST_MODULES + index + TEST_MODULES__ENABLED,
					Scan.getInstance().isModuleEnabled(module));
			int optionIndex = 0;
			for (ConfigurationOption option : module.getConfigurationOptions()) {
				optionIndex = saveTestModuleOption(config, index, optionIndex,
						option);
			}
			index++;
		}
	}

	public void setAcceptableAutomaticResponseCodeOverrideThreshold(
			final int acceptableAutomaticResponseCodeOverrideThreshold) {
		this.acceptableAutomaticResponseCodeOverrideThreshold = acceptableAutomaticResponseCodeOverrideThreshold;
		updateSettingsFile();
	}

	public void setAllowAllProxyRequests(final boolean allowAllProxyRequests) {
		this.allowAllProxyRequests = allowAllProxyRequests;
		updateSettingsFile();
	}

	public void setAuthenticationPackage(
			final AuthenticationPackage authenticationPackage) {
		this.authenticationPackage = authenticationPackage;
		updateSettingsFile();
	}

	// public void setOutputDirectory(String outputDirectory)
	// {
	// if (!outputDirectory.matches(File.separator + "$"))
	// {
	// outputDirectory += File.separator;
	// }
	// this.outputDirectory = outputDirectory;
	// }

	public final void setAutomaticAuthentication(
			final boolean automaticAuthentication) {
		if (automaticAuthentication) {
			Scan.getInstance().enableTestModule(AutoAuthentication.class);
		} else {
			try {
				Scan.getInstance().disableTestModule(AutoAuthentication.class);
			} catch (ModuleDependencyException e) {
				LOGGER.error(
						"Very weird problem disabling auto authentication: "
								+ e.toString(), e);
			}
		}

		this.automaticAuthentication = automaticAuthentication;
	}

	public void setInterceptRequests(final boolean interceptRequests) {
		this.interceptRequests = interceptRequests;
		updateSettingsFile();
	}

	public void setInterceptResponses(final boolean interceptResponses) {
		this.interceptResponses = interceptResponses;
		updateSettingsFile();
	}

	public final void setLogOutUri(final String logOutUri) {
		this.logOutUri = logOutUri;
	}

	public void setMaxCategorizerThreads(final int maxCategorizerThreads) {
		this.maxCategorizerThreads = maxCategorizerThreads;
		updateSettingsFile();
	}

	public void setMaxConnectionsPerServer(final int maxConnectionsPerServer) {
		this.maxConnectionsPerServer = maxConnectionsPerServer;
		updateSettingsFile();
	}

	public void setMaxConsecutiveFailedRequests(
			final int maxConsecutiveFailedRequests) {
		this.maxConsecutiveFailedRequests = maxConsecutiveFailedRequests;
		updateSettingsFile();
	}

	public void setMaxFailedRequestsPerHost(final int maxFailedRequestsPerHost) {
		this.maxFailedRequestsPerHost = maxFailedRequestsPerHost;
		updateSettingsFile();
	}

	public void setMaxFileSizeKiloBytes(final int maxFileSizeBytes) {
		maxFileSizeKiloBytes = maxFileSizeBytes;
		updateSettingsFile();
	}

	// public String generateDefaultOutputDirectory()
	// {
	// Date date = new Date();
	// SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
	// s.format(date);
	// return "scans" + File.separator + "grendel-scan-" + s.format(date);
	// }

	public void setMaxProxyThreads(final int maxProxyThreads) {
		this.maxProxyThreads = maxProxyThreads;
		updateSettingsFile();
	}

	public void setMaxRedirects(final int maxRedirects) {
		this.maxRedirects = maxRedirects;
		updateSettingsFile();
	}

	public void setMaxRequestDepth(final int maxRequestDepth) {
		this.maxRequestDepth = maxRequestDepth;
		updateSettingsFile();
	}

	/**
	 * @param maxRequesterThreads
	 *            the maxRequesterThreads to set
	 */
	public void setMaxRequesterThreads(final int maxRequesterThreads) {
		if (maxRequesterThreads > 0) {
			this.maxRequesterThreads = maxRequesterThreads;
			updateSettingsFile();
		}
	}

	public void setMaxRequestRetries(final int maxRequestRetries) {
		this.maxRequestRetries = maxRequestRetries;
		updateSettingsFile();
	}

	public void setMaxRequestsPerSecond(final int maxRequestsPerSecond) {
		this.maxRequestsPerSecond = maxRequestsPerSecond;
		updateSettingsFile();
	}

	public void setMaxTesterThreads(final int maxTesterThreads) {
		if (maxTesterThreads > 0) {
			this.maxTesterThreads = maxTesterThreads;
			updateSettingsFile();
		}
	}

	public void setMaxTotalConnections(final int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
		updateSettingsFile();
	}

	public void setParseHtmlDom(final boolean parseHtmlDom) {
		this.parseHtmlDom = parseHtmlDom;
		updateSettingsFile();
	}

	public void setProxyEnabled(final boolean proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
		updateSettingsFile();
	}

	public void setProxyIPAddress(final String proxyIPAddress) {
		this.proxyIPAddress = proxyIPAddress;
		updateSettingsFile();
	}

	public void setProxyPort(final int proxyPort) {
		this.proxyPort = proxyPort;
		updateSettingsFile();
	}

	public void setRevealHiddenFields(final boolean revealHiddenFields) {
		this.revealHiddenFields = revealHiddenFields;
		updateSettingsFile();
	}

	public void setSavedTextTransactionsDirectory(
			final String transactionTextDirectory) {
		savedTextTransactionsDirectory = transactionTextDirectory;
		updateSettingsFile();
	}

	public void setSocketReadTimeout(final int connectionTimeout) {
		socketReadTimeout = connectionTimeout;
		updateSettingsFile();
	}

	public final void setSocksHost(final String socksHost) {
		this.socksHost = socksHost;
	}

	public final void setSocksPort(final int socksPort) {
		this.socksPort = socksPort;
	}

	public void setTestAllDirectoriesForResponseCodeOverrides(
			final boolean checkAllDirectoriesForResponseCodeOverrides) {
		testAllDirectoriesForResponseCodeOverrides = checkAllDirectoriesForResponseCodeOverrides;
		updateSettingsFile();
	}

	public void setTestInterceptedRequests(final boolean testInterceptedRequests) {
		this.testInterceptedRequests = testInterceptedRequests;
		updateSettingsFile();
	}

	public void setTestManualRequests(final boolean testManualRequests) {
		this.testManualRequests = testManualRequests;
		updateSettingsFile();
	}

	public void setTestProxyRequests(final boolean testProxyRequests) {
		this.testProxyRequests = testProxyRequests;
		updateSettingsFile();
	}

	public void setUpstreamProxyAddress(final String upstreamProxyHost) {
		upstreamProxyAddress = upstreamProxyHost;
		updateSettingsFile();
	}

	public void setUpstreamProxyAuthenticationType(
			final HttpAuthenticationType upstreamProxyAuthenticationType) {
		this.upstreamProxyAuthenticationType = upstreamProxyAuthenticationType;
		updateSettingsFile();
	}

	public void setUpstreamProxyPassword(final String upstreamProxyPassword) {
		this.upstreamProxyPassword = upstreamProxyPassword;
		updateSettingsFile();
	}

	public void setUpstreamProxyPort(final int upstreamProxyPort) {
		this.upstreamProxyPort = upstreamProxyPort;
		updateSettingsFile();
	}

	public void setUpstreamProxyUsername(final String upstreamProxyUsername) {
		this.upstreamProxyUsername = upstreamProxyUsername;
		updateSettingsFile();
	}

	public void setUrlFilters(final UrlFilters urlFilters) {
		this.urlFilters = urlFilters;
		updateSettingsFile();
	}

	public void setUseAuthentication(final boolean useAuthentication) {
		this.useAuthentication = useAuthentication;
		updateSettingsFile();
	}

	public void setUseAutomaticResponseCodeOverrides(
			final boolean useAutomaticResponseCodeOverrides) {
		this.useAutomaticResponseCodeOverrides = useAutomaticResponseCodeOverrides;
		updateSettingsFile();
	}

	public void setUserAgentString(final String userAgentString) {
		this.userAgentString = userAgentString;
		updateSettingsFile();
	}

	public final void setUseSocksProxy(final boolean useSocksProxy) {
		this.useSocksProxy = useSocksProxy;
		// if (useSocksProxy)
		// {
		// System.setProperty("socksProxyHost", socksHost);
		// System.setProperty("socksProxyPort", Integer.toString(socksPort));
		// }
		// else
		// {
		// System.setProperty("socksProxyHost", "");
		// System.setProperty("socksProxyPort", "");
		// }
	}

	public void setUseUpstreamProxy(final boolean useUpstreamProxy) {
		this.useUpstreamProxy = useUpstreamProxy;
		updateSettingsFile();
	}

	public void updateSettingsFile() {
		try {
			saveScanSettings(Scan.getInstance().getOutputDirectory()
					+ Scan.settingsFile);
		} catch (ConfigurationException e) {
			LOGGER.error("Error updating settings file: " + e.toString(), e);
		}
	}

}
