import { CurrentSelectedCompanyService } from '../../../../../../main/webapp/app/shared/company/current-selected-company.service';
import { async, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { Observable } from 'rxjs';
import { Principal } from '../../../../../../main/webapp/app/shared';
import { CompanyService } from '../../../../../../main/webapp/app/shared/company/company.service';
import { UserInfoService } from '../../../../../../main/webapp/app/shared/user-info/user-info.service';
import { defer } from 'rxjs/observable/defer';
import { CompanyContactTemplateModel } from '../../../../../../main/webapp/app/shared/company/company-contact-template.model';
import { RegistrationStatus } from '../../../../../../main/webapp/app/shared/user-info/user-info.model';

describe('CurrentSelectedCompanyService', () => {

    const currentUser = {
        id: '1',
        login: 'login',
        firstName: 'first name',
        lastName: 'last name',
        email: 'email',
        langKey: 'lang',
        authorities: [],
        registrationStatus: RegistrationStatus.UNREGISTERED
    };

    const company = {
        companyExternalId: 'ext',
        companyId: 'id',
        companyName: 'name'
    };

    const contactTemplate = {
        companyId: 'companyId',
        companyName: 'companyName',
        companyStreet: 'companyStreet',
        companyHouseNr: '',
        companyZipCode: 'zip',
        companyCity: 'companyCity',
        phone: 'phone',
        email: currentUser.email,
        salutation: 'mr'
    };

    const mockPrincipal = jasmine.createSpyObj('mockPrincipal', ['getAuthenticationState', 'currentUser']);
    const mockCompanyService = jasmine.createSpyObj('mockCompanyService', ['findByExternalId']);
    const mockUserInfoService = jasmine.createSpyObj('mockUserInfoService', ['findCompanyContactTemplate', 'findAccountabilities']);

    function asyncData<T>(data: T) {
        return defer(() => Promise.resolve(data));
    }

    function asyncError<T>(errorObject: any) {
        return defer(() => Promise.reject(errorObject));
    }

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                CurrentSelectedCompanyService,
                { provide: Principal, useValue: mockPrincipal },
                { provide: CompanyService, useValue: mockCompanyService },
                { provide: UserInfoService, useValue: mockUserInfoService }
            ]
        }).compileComponents();
    });

    beforeEach(() => {
        mockPrincipal.getAuthenticationState.and.returnValue(asyncData({ id: '1' }));
        mockUserInfoService.findAccountabilities.and.returnValue(asyncData([company]));
        mockPrincipal.currentUser.and.returnValue(asyncData(currentUser));
        mockUserInfoService.findCompanyContactTemplate.and.returnValue(asyncData(contactTemplate));

        spyOn(CurrentSelectedCompanyService.prototype, 'selectAccountability').and.callThrough();
    });

    describe('constructor', () => {
        let currentSelectedCompanyService;
        beforeEach(() => {
            mockPrincipal.getAuthenticationState.and.callFake(() => Observable.of(null));
            currentSelectedCompanyService = TestBed.get(CurrentSelectedCompanyService);
        });

        it('getSelectedAccountability() should return null when current user is null',
            fakeAsync(() => {
                tick();
                currentSelectedCompanyService.getSelectedAccountability()
                    .takeLast(1)
                    .subscribe((selectedAccountability) => expect(selectedAccountability).toBeNull());
            })
        );

        it('getAccountabilities() should return null when current user is null',
            fakeAsync(() => {
                tick();
                currentSelectedCompanyService.getAccountabilities()
                    .takeLast(1)
                    .subscribe((accountabilities) => expect(accountabilities).toBeNull());
            })
        );

        it('getSelectedCompanyContactTemplate() should return null when current user is null',
            fakeAsync(() => {
                tick();
                currentSelectedCompanyService.getSelectedCompanyContactTemplate()
                    .takeLast(1)
                    .subscribe((selectedTemplate) => expect(selectedTemplate).toBeNull());
            })
        );
    });

    describe('getAccountabilities()', () => {
        it('should return loaded accountabilities', async(() => {
                // WHEN
                const currentSelectedCompanyService = TestBed.get(CurrentSelectedCompanyService);
                const accountabilities$ = currentSelectedCompanyService.getAccountabilities();

                // THEN
                accountabilities$.takeLast(1)
                    .subscribe((accountabilities) => expect(accountabilities).toEqual([company]))
            }
        ));
    });

    describe('selectAccountability()', () => {
        it('should throw error when selectedAccountability is null', async(() => {
            // WHEN
            const currentSelectedCompanyService = TestBed.get(CurrentSelectedCompanyService);

            // THEN
            expect(() => currentSelectedCompanyService.selectAccountability(null)).toThrowError('Accountability must not be null')
        }));

        it('should load company template', async(() => {
            // WHEN
            const currentSelectedCompanyService = TestBed.get(CurrentSelectedCompanyService);
            const contactTemplateModelObservable = currentSelectedCompanyService.selectAccountability(company);

            // THEN
            const expectedTemplate = new CompanyContactTemplateModel(contactTemplate, currentUser);
            contactTemplateModelObservable.takeLast(1)
                .subscribe((loadedTemplate) => expect(loadedTemplate).toEqual(expectedTemplate));
        }));

        it('should load default template on error', async(() => {
            // GIVEN
            mockUserInfoService.findCompanyContactTemplate.and.returnValue(asyncError(null));
            mockCompanyService.findByExternalId.and.returnValue(asyncData({
                id: 'companyId',
                name: 'companyName',
                street: 'companyStreet',
                zipCode: '62330',
                city: 'companyCity',
            }));

            // WHEN
            const currentSelectedCompanyService = TestBed.get(CurrentSelectedCompanyService);
            const contactTemplate$ = currentSelectedCompanyService.selectAccountability(company);

            const expectedTemplate = new CompanyContactTemplateModel({
                companyId: 'companyId',
                companyName: 'companyName',
                companyStreet: 'companyStreet',
                companyZipCode: '62330',
                companyCity: 'companyCity',
                email: currentUser.email
            }, currentUser);
            contactTemplate$.takeLast(1)
                .subscribe((loadedTemplate) => expect(loadedTemplate).toEqual(expectedTemplate))
        }))
    });

    describe('reset()', () => {
        it('getSelectedAccountability should return null', async(() => {
            const currentSelectedCompanyService = TestBed.get(CurrentSelectedCompanyService);
            currentSelectedCompanyService.reset();

            currentSelectedCompanyService.getSelectedAccountability().takeLast(1)
                .subscribe((selectedAccountability) => expect(selectedAccountability).toBeNull());
        }));

        it('getAccountabilities should return null', async(() => {
            const currentSelectedCompanyService = TestBed.get(CurrentSelectedCompanyService);
            currentSelectedCompanyService.reset();

            currentSelectedCompanyService.getAccountabilities().takeLast(1)
                .subscribe((accountabilities) => expect(accountabilities).toBeNull())
        }));

        it('getSelectedCompanyContactTemplate should return null', async(() => {
            const currentSelectedCompanyService = TestBed.get(CurrentSelectedCompanyService);
            currentSelectedCompanyService.reset();

            currentSelectedCompanyService.getSelectedCompanyContactTemplate().takeLast(1)
                .subscribe((companyTemplate) => expect(companyTemplate).toBeNull())
        }))
    });

    describe('reloadCurrentSelection()', () => {
        it('should call selectAccountability with previous loaded value', fakeAsync(() => {
            const currentSelectedCompanyService = TestBed.get(CurrentSelectedCompanyService);
            tick();
            currentSelectedCompanyService.reloadCurrentSelection();

            expect(currentSelectedCompanyService.selectAccountability).toHaveBeenCalledTimes(2);
        }));
    })
});
