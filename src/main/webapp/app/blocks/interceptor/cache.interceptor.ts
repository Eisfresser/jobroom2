import { Observable } from 'rxjs/Observable';
import { JhiBase64Service } from 'ng-jhipster/src/service/base64.service';
import { CookieService } from 'ngx-cookie';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';

export class CacheKeyInterceptor implements HttpInterceptor {
    constructor(private cookieService: CookieService, private base64: JhiBase64Service) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        let newRequest = req;

        const translateLangKey = this.cookieService.get('NG_TRANSLATE_LANG_KEY');
        if (translateLangKey) {
            newRequest = req.clone({
                setParams: { '_ng': this.base64.encode(translateLangKey) }
            });
        }
        return next.handle(newRequest);
    }
}
