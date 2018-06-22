import { SpyObject } from './spyobject';
import { Principal } from '../../../../main/webapp/app/shared';
import { Observable } from 'rxjs/Observable';
import Spy = jasmine.Spy;

export class MockPrincipal extends SpyObject {

    identitySpy: Spy;

    currentUser: Spy;

    constructor() {
        super(Principal);

        this.setIdentitySpy({});
    }

    setIdentitySpy(json: any): any {
        this.identitySpy = this.spy('identity').andReturn(Promise.resolve(json));
        this.currentUser = this.spy('currentUser').andReturn(Observable.of(json));
    }

    setResponse(json: any): void {
        this.setIdentitySpy(json);
    }
}
