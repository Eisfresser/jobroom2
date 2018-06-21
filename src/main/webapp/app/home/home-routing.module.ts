import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home.component';
import { UserDataResolverService } from './tools/job-publication-tool/service/user-data-resolver.service';
import { JobAdvertisementResolverService } from './tools/job-publication-tool/service/job-advertisement-resolver.service';

const routes: Routes = [
    {
        path: 'home',
        component: HomeComponent,
        data: {
            authorities: [],
            pageTitle: 'home.title'
        }
    },
    {
        path: 'login',              // for user/password login
        component: HomeComponent,
        data: {
            authorities: [],
            pageTitle: 'home.title'
        }
    },
    {
        path: 'jobseekers',
        component: HomeComponent,
        data: {
            authorities: [],
            pageTitle: 'home.title'
        }
    },
    {
        path: 'companies/jobpublication',
        component: HomeComponent,
        resolve: {
            userData: UserDataResolverService
        },
        data: {
            authorities: [],
            pageTitle: 'home.title'
        }
    },
    {
        path: 'companies/candidates',
        component: HomeComponent,
        data: {
            authorities: [],
            pageTitle: 'home.title'
        }
    },
    {
        path: 'agents/candidates',
        component: HomeComponent,
        data: {
            authorities: [],
            pageTitle: 'home.title'
        }
    },
    {
        path: 'agents/jobpublication',
        component: HomeComponent,
        resolve: {
            jobAdvertisement: JobAdvertisementResolverService,
            userData: UserDataResolverService
        },
        data: {
            authorities: [],
            pageTitle: 'home.title'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class HomeRoutingModule {
}
