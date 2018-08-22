package ch.admin.seco.jobroom.gateway;

import java.util.Set;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import org.springframework.stereotype.Component;

@Component
public class TokenRelayFilter extends ZuulFilter {

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Object headers = ctx.get("ignoredHeaders");
        if (headers != null){
            // JWT tokens should be relayed to the resource servers
            ((Set<String>) headers).remove("authorization");
        }
        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10000;
    }
}
