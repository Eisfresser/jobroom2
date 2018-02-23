import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';

import { VersionInfo } from './version-info.model';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class VersionService {

    private versionInfoUrl = 'management/info';

    constructor(private http: HttpClient) {
    }

    getVersionInfo(): Observable<VersionInfo> {
        return this.http.get(this.versionInfoUrl)
            .map((data: any) => {
                const pi = new VersionInfo('0.0.0-SNAPHOST');
                if (data.build) {
                    pi.version = data.build.version;
                    pi.artifact = data.build.artifact;
                    pi.group = data.build.group;
                    pi.buildTime = data.build.time;
                    pi.buildNumber = data.build.number;
                }
                if (data.git) {
                    pi.branch = data.git.branch;
                }
                return pi;
            });
    }
}
