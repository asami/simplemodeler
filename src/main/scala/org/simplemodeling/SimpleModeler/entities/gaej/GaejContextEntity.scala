package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker}

/*
 * @since   Oct.  2, 2009
 * @version Nov. 12, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejContextEntity(aContext: GaejEntityContext) extends GaejObjectEntity(aContext) {
  val services = new ArrayBuffer[GaejServiceEntity]

  private val code = """package %packageName%;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.jdo.JDODetachedFieldAccessException;
import javax.jdo.JDONullIdentityException;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.JDOQueryInterruptedException;
import javax.jdo.JDOQueryTimeoutException;
import javax.jdo.JDOReadOnlyException;
import javax.jdo.JDOUnsupportedOptionException;
import javax.jdo.JDOUserCallbackException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.datanucleus.exceptions.NoPersistenceInformationException;
import org.datanucleus.exceptions.TransactionNotActiveException;
import org.datanucleus.jdo.exceptions.ClassNotPersistenceCapableException;
import org.datanucleus.jdo.exceptions.ConnectionInUseException;
import org.datanucleus.jdo.exceptions.TransactionActiveException;
import org.datanucleus.jdo.exceptions.TransactionCommitingException;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskHandle;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Method.*;

@SuppressWarnings("unused")
public class %contextName% {
    private final %factoryName% factory;
    private final UserService userService = UserServiceFactory.getUserService();
    private final MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
    private final Set<Object> flushCacheKeys = new HashSet<Object>();

    private boolean useAssociationCache = true;
    private long associationCachePeriod = 24 * 60 * 60; // 86400000

    public %contextName%(%factoryName% aFactory) {
        factory = aFactory;
    }

    public final %factoryName% getFactory() {
        return factory;
    }

    // entity
    public boolean isAssociationCache(Object object, Date timestamp) {
    	return useAssociationCache && Util.isAvailableTimestamp(timestamp, associationCachePeriod);
    }

    // service
    public Queue getMainQueue() {
    	return QueueFactory.getDefaultQueue();
    }

    public Queue getLogQueue() {
    	return QueueFactory.getDefaultQueue();
    }

    public Queue getQueue(String name) {
    	return QueueFactory.getQueue(name);
    }

    @SuppressWarnings("unchecked")
    public void asyncAction(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String path = req.getRequestURI();
        int index = path.indexOf("-async");
        path = path.substring(0, index);
        Queue queue = getMainQueue();
        TaskOptions options = url(path.toString());
        String uuid = UUID.randomUUID().toString();
        options = options.taskName(uuid);
        Set<Map.Entry<String, String[]>> entries = req.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                options = options.param(key, value);
            }
        }
        options = options.param("task_queue", queue.getQueueName());
        options = options.param("task_name", uuid);
        TaskHandle task = queue.add(options);
        PrintWriter buf = resp.getWriter();
        buf.append("{");
        buf.append("  result_code = ");
        buf.append(acceptedResultCode());
        buf.append(",");
        buf.append("  task_queue = \"");
        buf.append(task.getQueueName());
        buf.append("\"");
        buf.append("  task_name = \"");
        buf.append(task.getName());
        buf.append("\"");
        buf.append("}");
        buf.append("\n");
    }
  
    public void completeTask(HttpServletRequest req, String inXml, String outXml, long startTime, long endTime) {
        TaskEvent task = new_task_event(req, startTime, endTime);
        task.in = inXml;
        task.out = outXml;
        log_task(req, task);
    }

    public void completeTask(HttpServletRequest req, String inXml, long startTime, long endTime) {
        TaskEvent task = new_task_event(req, startTime, endTime);
        task.in = inXml;
        log_task(req, task);
    }

    @SuppressWarnings("unchecked")
    private TaskEvent new_task_event(HttpServletRequest req, long startTime,
            long endTime) {
        TaskEvent task = new TaskEvent();
        task.startTime = startTime;
        task.endTime = endTime;
        task.requestURL = req.getRequestURL().toString();
        task.requestURI = req.getRequestURI();
        task.contextPath = req.getContextPath();
        task.servletPath = req.getServletPath();
        task.pathInfo = req.getPathInfo();
        task.scheme = req.getScheme();
        task.protocol = req.getProtocol();
        task.queryString = req.getQueryString();
        task.method = req.getMethod();
        task.authType = req.getAuthType();
        task.characterEncoding = req.getCharacterEncoding();
        task.contentType = req.getContentType();
        task.contentLength = req.getContentLength();
        task.addLocales(req.getLocales());
        task.addCookies(req.getCookies());
        task.requestedSessionId = req.getRequestedSessionId();
        task.session = req.getSession();
        task.principal = req.getUserPrincipal();
        task.localAddr = req.getLocalAddr();
        task.localName = req.getLocalName();
        task.localPort = req.getLocalPort();
        task.remoteAddr = req.getRemoteAddr();
        task.remoteHost = req.getRemoteHost();
        task.remotePort = req.getRemotePort();
        task.remoteUser = req.getRemoteUser();
        task.serverName = req.getServerName();
        task.serverPort = req.getServerPort();
        Enumeration<String> headers = req.getHeaderNames();
        if (headers != null) {
            while (headers.hasMoreElements()) {
                String header = headers.nextElement();
                String value = req.getHeader(header);
                task.addServletHeader(header, value);
            }
        }
        Enumeration<String> parameters = req.getParameterNames();
        while (parameters.hasMoreElements()) {
            String param = parameters.nextElement();
            String[] values = req.getParameterValues(param);
            task.addServletParameter(param, values);
        }
        Enumeration<String> attrs = req.getAttributeNames();
        while (attrs.hasMoreElements()) {
            String attr = attrs.nextElement();
            Object value = req.getAttribute(attr);
            task.addServletAttribute(attr, value);
        }
        task.user = userService.getCurrentUser();
        return task;
    }

    public void exceptionTask(HttpServletRequest req, String inXml, Exception e, long startTime, long endTime) {
        TaskEvent task = new_task_event(req, startTime, endTime);
        task.in = inXml;
        task.exception = e;
        log_task(req, task);
    }

    public void exceptionTask(HttpServletRequest req, Exception e, long startTime, long endTime) {
        TaskEvent task = new_task_event(req, startTime, endTime);
        task.exception = e;
        log_task(req, task);
    }

    private void log_task(HttpServletRequest req, TaskEvent task) {
        if (is_async(req)) {
            logTaskEvent(task);
        } else {
            log_task_async(req, task);
        }
    }

    private boolean is_async(HttpServletRequest req) {
        String aeQueueName = req.getHeader("X-AppEngine-QueueName");
        String aeTaskName = req.getHeader("X-AppEngine-TaskName");
        String paramTaskQueue = req.getParameter("task_queue");
        String paramTaskName = req.getParameter("task_name");
        return (aeQueueName != null && aeTaskName != null) || (paramTaskQueue != null && paramTaskName != null);
    }

    private void log_task_async(HttpServletRequest req, TaskEvent task) {
        String contextPath = req.getContextPath();
        if (contextPath == null || "/".equals(contextPath)) {
          contextPath = "";
        }
        String path = contextPath + req.getServletPath() + "/_system/event/log";
        Queue queue = getLogQueue();
        queue.add(url(path.toString()).taskName(task.id).payload(task.toXmlString()).method(POST));
    }

    public void logTaskEvent(TaskEvent task) {
        logEvent(task.toXmlString());
    }

    public void logEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logEvent(req);
        resultOk(resp);
    }

    public void logEvent(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        char[] chars = new char[8192];
        StringBuilder buf = new StringBuilder();
        int count;
        while ((count = reader.read(chars)) != -1) {
            buf.append(chars, 0, count);
        }
        String xml = buf.toString();
        logEvent(xml);
    }

    public void logEvent(String xml) {
        String pattern = "id=\"";
        int start = xml.indexOf(pattern);
        int end = xml.indexOf("\"", start + pattern.length());
        String id = xml.substring(start + pattern.length(), end);
        PersistenceManager pm = Util.getPersistenceManager();
        try {
            %eventName% event = new %eventName%(id, xml);
            pm.makePersistent(event);
        } finally {
            pm.close();
        }
    }

    public void listEvent(HttpServletRequest req, HttpServletResponse resp) {
      // XXX
    }

    public void readEvent(String id, HttpServletRequest req, HttpServletResponse resp) {
        // XXX
    }

    public void setFlushCacheKey(String key) {
        flushCacheKeys.add(key);
    }

    public void flush(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        flushCache(flushCacheKeys);
        resultOk(resp);
    }
  
    public void systemAction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] path = get_path(req);
        if (!"_system".equals(path[0])) {
            throw new IOException("not _system"); // XXX
        }
        if ("event".equals(path[1])) {
            if (path.length == 2) {
                listEvent(req, resp);
            } else if ("log".equals(path[2])) {
                logEvent(req, resp);
            } else {
                String id = path[2];
                readEvent(id, req, resp);
            }
        } else if ("flush".equals(path[1])) {
            flush(req, resp);
        } else {
            throw new IOException("not event"); // XXX
        }
    }

    private String[] get_path(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) {
            return new String[0];
        } else if (path.length() == 0) {
            return new String[0];
        } else if ("/".equals(path)) {
            return new String[0];
        } else if (path.startsWith("/")) {
            return path.substring(1).split("/");
        } else {
            return path.split("/");
        }
    }

    static class TaskEvent {
        public String id = UUID.randomUUID().toString();
        public long startTime;
        public long endTime;
        public String in;
        public String out;
        public Exception exception;
        public String requestURL;
        public String requestURI;
        public String contextPath;
        public String servletPath;
        public String pathInfo;
        public String scheme;
        public String protocol;
        public String queryString;
        public String method;
        public String authType;
        public String characterEncoding;
        public String contentType;
        public int contentLength;
        public List<Locale> locales = new ArrayList<Locale>();
        public List<Cookie> cookies = new ArrayList<Cookie>();
        public String requestedSessionId;
        public HttpSession session;
        public Principal principal;
        public String localAddr;
        public String localName;
        public int localPort;
        public String remoteAddr;
        public String remoteHost;
        public int remotePort;
        public String remoteUser;
        public String serverName;
        public int serverPort;
        public Map<String, String> headers = new LinkedHashMap<String, String>();
        public Map<String, String> parameters = new LinkedHashMap<String, String>();
        public Map<String, String> attributes = new LinkedHashMap<String, String>();
        public User user;

        public void addLocales(Enumeration<Locale> locales) {
            while (locales.hasMoreElements()) {
                this.locales.add(locales.nextElement());
            }
        }

        public void addCookies(Cookie[] cookies) {
            if (cookies == null) return;
            this.cookies.addAll(Arrays.asList(cookies));
        }

        public void addServletHeader(String header, String value) {
            headers.put(header, value);
        }

        public void addServletParameter(String param, String[] values) {
            if (values == null || values.length == 0) {
                parameters.put(param, "");
            } else if (values.length == 1) {
                parameters.put(param, values[0]);
            } else {
                StringBuilder buf = new StringBuilder();
                buf.append(values[0]);
                for (int i = 1;i < values.length;i++) {
                    buf.append(' ');
                    buf.append(values[i]); // XXX normalize
                }
                parameters.put(param, buf.toString());
            }
        }

        public void addServletAttribute(String attr, Object value) {
            attributes.put(attr, value.toString());
        }

        public String toXmlString() {
            XmlMaker maker = new XmlMaker();
            return maker.toString();
        }

        class XmlMaker {
            StringBuilder buf = new StringBuilder();
            
            public String toString() {
                Map<String, String> attrs = new LinkedHashMap<String, String>();
                attrs.put("id", id);
                attrs.put("start-time", to_datetime(startTime));
                attrs.put("end-time", to_datetime(endTime));
                open_tag("task", attrs);
                tag("requestURL", requestURL);
                tag("requestURI", requestURI);
                tag("contextPath", contextPath);
                tag("servletPath", servletPath);
                tag("pathInfo", pathInfo);
                tag("scheme", scheme);
                tag("protocol", protocol);
                tag("queryString", queryString);
                tag("method", method);
                tag("authType", authType);
                tag("characterEncoding", characterEncoding);
                tag("contentType", contentType);
                tag("contentLength", contentLength);
                make_locales();
                make_cookies();
                tag("requestedSessionId", requestedSessionId);
                make_session();
                make_principal();
                tag("localAddr", localAddr);
                tag("localName", localName);
                tag("localPort", localPort);
                tag("remoteAddr", remoteAddr);
                tag("remoteHost", remoteHost);
                tag("remotePort", remotePort);
                tag("remoteUser", remoteUser);
                tag("serverName", serverName);
                tag("serverPort", serverPort);
                make_headers();
                make_parameters();
                make_attributes();
                make_user();
                make_task_queue();
                close_tag("task");
                return buf.toString();
            }

            private void make_locales() {
                open_tag("locale");
                boolean first = true;
                for (Locale locale: locales) {
                    if (first) {
                        first = false;
                    } else {
                        delimiter();
                    }
                    value(locale.toString());
                }
                close_tag("locale");
            }

            private void make_cookies() {
                for (Cookie cookie: cookies) {
                    Map<String, String> attrs = new LinkedHashMap<String, String>();
                    attrs.put("name", cookie.getName());
                    attrs.put("comment", cookie.getComment());
                    tag("cookie", attrs, cookie.getValue());
                }
            }

            private void make_session() {
                if (session == null) return;
                Map<String, String> attrs = new LinkedHashMap<String, String>();
                attrs.put("id", session.getId());
                attrs.put("creationTime", to_datetime(session.getCreationTime()));
                attrs.put("lastAccessTime", to_datetime(session.getLastAccessedTime()));
                attrs.put("maxIntaractiveInterval", to_datetime(session.getMaxInactiveInterval()));
                tag("session", attrs);
            }

            private String to_datetime(long dateTime) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                return df.format(new Date(dateTime));
            }

            private void make_principal() {
                if (principal == null) return;
                tag("principal", principal.getName());
            }

            private void make_headers() {
                for (Map.Entry<String, String> header: headers.entrySet()) {
                    Map<String, String> attrs = new LinkedHashMap<String, String>();
                    attrs.put("name", header.getKey());
                    tag("cookie", attrs, header.getValue());
                }
            }

            private void make_parameters() {
                for (Map.Entry<String, String> parameter: parameters.entrySet()) {
                    Map<String, String> attrs = new LinkedHashMap<String, String>();
                    attrs.put("name", parameter.getKey());
                    tag("cookie", attrs, parameter.getValue());
                }
            }

            private void make_attributes() {
                for (Map.Entry<String, String> attribute: attributes.entrySet()) {
                    Map<String, String> attrs = new LinkedHashMap<String, String>();
                    attrs.put("name", attribute.getKey());
                    tag("cookie", attrs, attribute.getValue());
                }
            }

            private void make_user() {
                if (user == null) return;
                Map<String, String> attrs = new LinkedHashMap<String, String>();
                attrs.put("authDomain", user.getAuthDomain());
                attrs.put("email", user.getEmail()); 
                attrs.put("nickname", user.getNickname()); 
                attrs.put("userId", user.getUserId());
                tag("user", attrs);
            }

            private void make_task_queue() {
                String aeQueueName = headers.get("X-AppEngine-QueueName");
                String aeTaskName = headers.get("X-AppEngine-TaskName");
                String aeRetryCount = headers.get("X-AppEngine-TaskRetryCount");
                String paramTaskQueue = parameters.get("task_queue");
                String paramTaskName = parameters.get("task_name");
                if (aeQueueName == null && aeTaskName == null && aeRetryCount == null &&
                        paramTaskQueue == null && paramTaskName == null) {
                    return;
                }
                Map<String, String> attrs = new LinkedHashMap<String, String>();
                attrs.put("X-AppEngine-QueueName", aeQueueName);
                attrs.put("X-AppEngine-TaskName", aeTaskName);
                attrs.put("X-AppEngine-TaskRetryCount", aeRetryCount);
                attrs.put("task_queue", paramTaskQueue);
                attrs.put("task_name", paramTaskName);
                tag("taskQueue", attrs);
            }

            private void open_tag(String name) {
                buf.append("<");
                buf.append(name);
                buf.append(">");
            }

            private void open_tag(String name, Map<String, String> attrs) {
                buf.append("<");
                buf.append(name);
                for (Map.Entry<String, String> attr: attrs.entrySet()) {
                    String value = attr.getValue(); 
                    if (value != null) {
                        buf.append(" ");
                        buf.append(attr.getKey());
                        buf.append("=\"");
                        buf.append(value);
                        buf.append("\"");
                    }
                }
                buf.append(">");
            }

            private void close_tag(String name) {
                buf.append("</");
                buf.append(name);
                buf.append(">");
            }

            private void tag(String name, String value) {
                open_tag(name);
                value(value);
                close_tag(name);
            }

            private void tag(String name, int value) {
                open_tag(name);
                value(value);
                close_tag(name);
            }

            private void tag(String name, Map<String, String> attrs, String value) {
                open_tag(name, attrs);
                value(value);
                close_tag(name);
            }

            private void tag(String name, Map<String, String> attrs) {
                buf.append("<");
                buf.append(name);
                for (Map.Entry<String, String> attr: attrs.entrySet()) {
                    String value = attr.getValue(); 
                    if (value != null) {
                        buf.append(" ");
                        buf.append(attr.getKey());
                        buf.append("=\"");
                        buf.append(value);
                        buf.append("\"");
                    }
                }
                buf.append("/>");
            }

            private void value(String value) {
                buf.append(value);
            }

            private void value(int value) {
                buf.append(value);
            }

            private void delimiter() {
                buf.append(' ');
            }
        }
    }

    public void resultOk(HttpServletResponse resp) throws IOException {
        String code = okResultCode();
        PrintWriter buf = resp.getWriter();
        buf.append("{");
        buf.append("  result_code = ");
        buf.append(code);
        buf.append("}");
        buf.append("\n");
        buf.flush();
    }

    public void resultException(HttpServletResponse resp, Exception e) throws IOException {
        String code = exceptionResultCode(e);
        PrintWriter buf = resp.getWriter();
        buf.append("{");
        buf.append("  result_code = ");
        buf.append(code);
        buf.append(",");
        buf.append("  result_message = \"");
        buf.append(e.getMessage());
        buf.append("\"");
        buf.append("}");
        buf.append("\n");
    }

    public String exceptionResultCode(Exception e) {
        if (e instanceof javax.jdo.JDOFatalException) {
            return "500";
        } else if (e instanceof JDOObjectNotFoundException) {
            return "404";
        } else if (e instanceof ClassNotPersistenceCapableException) {
            return "500";
        } else if (e instanceof ConnectionInUseException) {
            return "500";
        } else if (e instanceof JDODetachedFieldAccessException) {
            return "500";
        } else if (e instanceof JDONullIdentityException) {
            return "500";
        } else if (e instanceof JDOQueryInterruptedException) {
            return "500";
        } else if (e instanceof JDOQueryTimeoutException) {
            return "500";
        } else if (e instanceof JDOReadOnlyException) {
            return "500";
        } else if (e instanceof JDOUnsupportedOptionException) {
            return "500";
        } else if (e instanceof JDOUserCallbackException) {
            return "500";
        } else if (e instanceof NoPersistenceInformationException) {
            return "500";
        } else if (e instanceof TransactionActiveException) {
            return "500";
        } else if (e instanceof TransactionCommitingException) {
            return "500";
        } else if (e instanceof TransactionNotActiveException) {
            return "500";
        } else if (e instanceof javax.jdo.JDOCanRetryException) {
            return "500";
        } else {
            return "500";
        }
    }

    public String okResultCode() {
        return "200";
    }

    public String createdResultCode() {
        return "201";
    }

    public String acceptedResultCode() {
        return "202";
    }

    public String partialContentResultCode() {
        return "206";
    }

    // cache
    public void putCache(String key, Object value, Runnable flusher) {
        CacheControlBlock ccb = (CacheControlBlock)memcache.get(key);
        if (ccb == null) {
            ccb = new CacheControlBlock();
        }
        ccb.isDirty = true;
        ccb.value = value;
        ccb.flusher = flusher;
        memcache.put(key, ccb);
    }

    public Object getCache(String key) {
        CacheControlBlock ccb = (CacheControlBlock)memcache.get(key);
        if (ccb == null) {
            return null;
        } else {
            return ccb.value;
        }
    }

    public boolean tryLockRead(String key) {
        int lockCounterExpirationSeconds = 60; // XXX
        try {
            Long count = memcache.increment(key, 16);
            if (count == null) {
                memcache.put(key, new Long(0), Expiration.byDeltaSeconds(lockCounterExpirationSeconds), MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
                count = memcache.increment(key, 16);
            }
            if ((count & 7) == 0) {
                return true;
            } else {
                memcache.increment(key, -16);
                return false;
            }
        } catch (Exception e) {
            memcache.put(key, new Long(0), Expiration.byDeltaSeconds(lockCounterExpirationSeconds), MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
            return false;
        }
    }

    public boolean tryLockWrite(String key) {
        int lockCounterExpirationSeconds = 60; // XXX
        try {
            Long count = memcache.increment(key, 1);
            if (count == null) {
                memcache.put(key, new Long(0), Expiration.byDeltaSeconds(lockCounterExpirationSeconds), MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
                count = memcache.increment(key, 1);
            }
            if (count == 1) {
                return true;
            } else {
                memcache.increment(key, -1);
                return false;
            }
        } catch (Exception e) {
            memcache.put(key, new Long(0), Expiration.byDeltaSeconds(lockCounterExpirationSeconds), MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
            return false;
        }
    }

    public boolean lockRead(String key) {
        int lockRetryCount = 10; // XXX
        int lockWaitMillSec = 500; // XXX
        while (lockRetryCount-- > 0) {
            try {
                if (tryLockRead(key)) {
                    return true;
                }
                Thread.sleep(lockWaitMillSec);
            } catch (Exception e) {}
        }
        return false;    
    }

    public boolean lockWrite(String key) {
        int lockRetryCount = 10; // XXX
        int lockWaitMillSec = 500; // XXX
        while (lockRetryCount-- > 0) {
            try {
                if (tryLockWrite(key)) {
                    return true;
                }
                Thread.sleep(lockWaitMillSec);
            } catch (Exception e) {}
        }
        return false;    
    }

    public void unlockRead(String key) {
        memcache.increment(key, -16);
    }

    public void unlockWrite(String key) {
        memcache.increment(key, -1);
    }

    public void executeWriteAction(String key, Runnable action) {
        if (!lockWrite(key)) {
            throw new IllegalStateException("can't lock = " + key);
        }
        try {
            action.run();
        } finally {
            unlockRead(key);
        }
    }

    public void executeReadAction(String key, Runnable action) {
        if (!lockRead(key)) {
            throw new IllegalStateException("can't lock = " + key);
        }
        try {
            action.run();
        } finally {
            unlockRead(key);
        }
    }

    public void flushCache(Collection<Object> keys) {
        for (Object key: keys) {
            String lockKey = key.toString() + "_lock";
            boolean locked = tryLockRead(lockKey);
            if (locked) {
                try {
                    CacheControlBlock ccb = (CacheControlBlock)memcache.get(key);
                    if (ccb != null && ccb.isDirty) {
                        ccb.isDirty = false;
                        try {
                            ccb.flusher.run();
                        } catch (Exception e) {
                            ccb.isDirty = true;
                        }
                    }
                } finally {
                    unlockRead(lockKey);
                }
            }
        }
    }

    static class CacheControlBlock implements Serializable {
        public Object value;
        public boolean isDirty = false;
        public Runnable flusher;
    }

%creators%
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val maker = new JavaTextMaker(
      code,
      Map("%packageName%" -> packageName,
          "%contextName%" -> name,
          "%eventName%" -> gaejContext.eventName(packageName),
          "%factoryName%" -> gaejContext.factoryName(packageName)))
    maker.replace("%creators%")(make_creators)
    out.append(maker.toString)
    out.flush()
  }

  private def make_creators(code: JavaTextMaker) {
    def make_service_creator(aService: GaejServiceEntity) {
      val methodSignature = "create" + gaejContext.serviceBaseName(aService) + "()"
      code.method("public " + aService.name + " " + methodSignature) {
        code.makeVar("service", aService.name, "factory." + methodSignature)
        code.println("service.setContext(this);")
        code.makeReturn("service")
      }
    }

    services.foreach(make_service_creator)
  }
}
