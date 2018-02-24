import { HttpParams } from '@angular/common/http';

export const createRequestOption = (req?: any): HttpParams => {
    let options: HttpParams = new HttpParams();
    if (req) {
        Object.keys(req).forEach((key) => {
            if (key !== 'sort') {
                options = options.set(key, req[key]);
            }
        });
        if (req.sort) {
            req.sort.forEach((val) => {
                options = options.append('sort', val);
            });
        }
    }
    return options;
};

export const createPageableURLSearchParams = (req?: any): HttpParams => {
    let params = new HttpParams()
        .set('page', req.page)
        .set('size', req.size);
    if (req.sort) {
        if (req.sort instanceof Array) {
            req.sort.forEach((sort) => params = params.append('sort', sort));
        } else {
            params = params.set('sort', req.sort);
        }
    }
    return params;
};
