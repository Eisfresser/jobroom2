import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { SERVER_API_URL } from '../../app.constants';
import { ProfileInfo } from './profile-info.model';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class ProfileService {

    private profileInfoUrl = SERVER_API_URL + 'api/profile-info';
    private profileInfo: Observable<ProfileInfo>;

    constructor(private http: HttpClient) { }

    getProfileInfo(): Observable<ProfileInfo> {
        if (!this.profileInfo) {
            this.profileInfo = this.http.get<ProfileInfo>(this.profileInfoUrl)
                .map((data) => {
                    const pi = new ProfileInfo();
                    pi.activeProfiles = data.activeProfiles;
                    pi.ribbonEnv = data.ribbonEnv;
                    pi.inProduction = data.activeProfiles.includes('prod') ;
                    pi.swaggerEnabled = data.activeProfiles.includes('swagger');
                    return pi;
                });
        }
        return this.profileInfo;
    }
}
