import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home.component';
import { JobPublicationResolverService } from './tools/job-publication-tool/service/job-publication-resolver.service';
import { UserDataResolverService } from './tools/job-publication-tool/service/user-data-resolver.service';
import { AuthResolverService } from './tools/auth-resolver.service';

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
        path: 'auth',
        component: HomeComponent,
        data: {
            authorities: [],
            pageTitle: 'home.title'
        },
        resolve: {
            auth: AuthResolverService
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
            jobPublication: JobPublicationResolverService,
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
