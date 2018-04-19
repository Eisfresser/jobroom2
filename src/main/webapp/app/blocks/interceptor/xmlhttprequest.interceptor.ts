import { Observable } from 'rxjs/Observable';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
// import { SERVER_API_URL } from '../../app.constants';

export class XmlhttprequestInterceptor implements HttpInterceptor {

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        /*if (!request || !request.url || (/^http/.test(request.url) && !(SERVER_API_URL && request.url.startsWith(SERVER_API_URL)))) {
            return next.handle(request);
        }*/

           const custRequest = request.clone({
                setHeaders: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

        return next.handle(custRequest);
    }

}
