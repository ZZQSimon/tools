package cn.com.easyerp.core.velocity;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.SystemUtils;
import org.apache.velocity.*;
import org.apache.velocity.app.*;
import org.apache.velocity.context.*;
import org.apache.velocity.tools.view.*;
import org.apache.velocity.tools.view.context.*;
import org.apache.velocity.tools.view.servlet.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import cn.com.easyerp.DeployTool.service.ImportDeployService;
import cn.com.easyerp.DeployTool.view.ImportDeployModel;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.dao.ApproveDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.core.logger.Loggable;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.module.ModuleService;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.generate.AutoGenerateFormModel;

@Service
public class DxToolService {
    public static final FilenameFilter jsFilter;
    public static final String TAG = "DxToolService";
    public static final String TOOLBOX_PATH = "/WEB-INF/velocity/toolbox.xml";
    private static final String DX_TOOL_FORM_MODEL_CACHE_KEY = "DX_TOOL_FORM_MODEL_CACHE_KEY";
    private static final String[] vmFieldRenderArgs;
    @Autowired
    DataService dataService;
    @Loggable
    Logger logger;
    @Autowired
    AuthService authService;
    @Autowired
    ViewService viewService;
    @Autowired
    DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    VelocityEngine velocityEngine;
    @Autowired
    ModuleService moduleService;
    Set<String> jsList;
    Set<String> cssList;
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private ApproveDao approveDao;
    @Autowired
    private ImportDeployService importDeployService;
    @Autowired
    CacheService cacheService;
    private DxTool tool;
    private static final String MODULE_RESOURCE_PREFIX = "/cn/com/easyerp/module/resource";

    @PostConstruct
    public void init() throws IOException {
        final String context = this.servletContext.getContextPath();
        final File[] list = new File(this.servletContext.getRealPath("js")).listFiles(DxToolService.jsFilter);
        this.jsList = new LinkedHashSet<String>();
        for (final File file : list) {
            this.jsList.add(context + "/js/" + file.getName());
        }
        this.cssList = new LinkedHashSet<String>();
        for (final String js : this.moduleService.getModuleJs()) {
            this.jsList.add(context + js);
        }
        for (final String css : this.moduleService.getModuleCss()) {
            this.cssList.add(context + css);
        }
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            final Resource[] resources = resolver.getResources("/cn/com/easyerp/module/resource/js/*.js");
            for (final Resource resource : resources) {
                String path = resource.getFile().getPath();
                if (SystemUtils.IS_OS_WINDOWS) {
                    path = path.replace("\\", "/");
                }
                final int idx = path.lastIndexOf(MODULE_RESOURCE_PREFIX) + MODULE_RESOURCE_PREFIX.length();
                this.jsList.add(context + path.substring(idx));
            }
        } catch (IOException ex) {
        }
        try {
            final Resource[] resources = resolver.getResources("/cn/com/easyerp/module/resource/css/*.css");
            for (final Resource resource : resources) {
                final String path = resource.getFile().getPath();
                final int idx = path.indexOf(MODULE_RESOURCE_PREFIX) + MODULE_RESOURCE_PREFIX.length();
                this.cssList.add(context + path.substring(idx));
            }
        } catch (IOException ex2) {
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Context createVelocityContext(final Map<String, Object> model, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final ChainedContext velocityContext = new ChainedContext((Context) new VelocityContext((Map) model),
                this.velocityEngine, request, response, this.servletContext);
        final ToolboxManager toolboxManager = (ToolboxManager) ServletToolboxManager.getInstance(this.servletContext,
                "/WEB-INF/velocity/toolbox.xml");
        final Map<?, ?> toolboxContext = (Map<?, ?>) toolboxManager.getToolbox((Object) velocityContext);
        velocityContext.setToolbox((Map) toolboxContext);
        return (Context) velocityContext;
    }

    public String macro(final String name, final Map<String, Object> map, final HttpServletRequest req,
            final HttpServletResponse resp) {
        try {
            final Context context = this.createVelocityContext(map, req, resp);
            final StringWriter sw = new StringWriter();
            final String[] keys = new String[map.size()];
            final Object[] keySet = map.keySet().toArray();
            for (int i = 0; i < keySet.length; ++i) {
                keys[i] = (String) keySet[i];
            }
            this.velocityEngine.invokeVelocimacro(name, "DxToolService", keys, context, (Writer) sw);
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException((Throwable) e);
        }
    }

    public String field(final FieldModelBase field, final AutoGenerateFormModel form) {
        final VelocityContext context = new VelocityContext();
        final ColumnDescribe column = this.dataService.getColumnDesc(field);
        context.put("field", (Object) field);
        context.put("desc", (Object) column);
        context.put("form", (Object) form);
        context.put("inputDivClass", (Object) "");
        context.put("class", (Object) "ui-widget dx-field dx-field-12");
        context.put("dx", (Object) this.tool);
        final StringWriter sw = new StringWriter();
        try {
            velocityEngine.invokeVelocimacro("dxFieldWithPrefixSuffix", "ril", DxToolService.vmFieldRenderArgs,
                    (Context) context, (Writer) sw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

    public String field(final FieldModelBase field) {
        try {
            return field(field, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setTool(final DxTool tool) {
        this.tool = tool;
    }

    @SuppressWarnings("unchecked")
    public void cacheFormModel(final FormModelBase form) {
        Map<String, FormModelBase> formModelCache = (Map<String, FormModelBase>) Common
                .getSessionObject(DX_TOOL_FORM_MODEL_CACHE_KEY);
        if (formModelCache == null) {
            Common.putSessionObject(DX_TOOL_FORM_MODEL_CACHE_KEY,
                    (Object) (formModelCache = new HashMap<String, FormModelBase>()));
        }
        formModelCache.put(form.getId(), form);
    }

    @SuppressWarnings("unchecked")
    public FormModelBase getCachedFormModel(final String id) {
        final Map<String, FormModelBase> formModelCache = (Map<String, FormModelBase>) Common
                .getSessionObject(DX_TOOL_FORM_MODEL_CACHE_KEY);
        return formModelCache.remove(id);
    }

    public int ifAllow(final String approveId) {
        return this.approveDao.ifAllow(approveId);
    }

    public ImportDeployModel getImportDeploy(final String table) {
        return this.importDeployService.getImportDeploy(table);
    }

    public CacheService getCacheService() {
        return this.cacheService;
    }

    static {
        jsFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.startsWith("g1-") && name.endsWith(".js");
            }
        };
        vmFieldRenderArgs = new String[] { "field", "desc", "inputDivClass", "class" };
    }
}
