import { Injectable, Renderer2, RendererFactory2 } from '@angular/core';
import { ToolbarItem } from '../../home/state-management/state/layout.state';

const BACKGROUND_CLASS_NAME_ARRAY = [];
BACKGROUND_CLASS_NAME_ARRAY[ToolbarItem.JOB_SEEKERS] = 'background--jobseeker';
BACKGROUND_CLASS_NAME_ARRAY[ToolbarItem.COMPANIES] = 'background--companies';
BACKGROUND_CLASS_NAME_ARRAY[ToolbarItem.RECRUITMENT_AGENCIES] = 'background--pea';

@Injectable()
export class BackgroundUtils {
    private renderer: Renderer2;

    constructor(rendererFactory: RendererFactory2) {
        this.renderer = rendererFactory.createRenderer(null, null);
    }

    addBackGroundClass(toolbarItem: ToolbarItem) {
        const className = BACKGROUND_CLASS_NAME_ARRAY[toolbarItem];
        this.removeBackgroundClass(className);
        if (className) {
            this.renderer.addClass(document.body, className);
        }
    }

    addBackgroundForJobseekers() {
        this.addBackGroundClass(ToolbarItem.JOB_SEEKERS);
    }

    removeBackgroundClass(className: string) {
        if (this.renderer) {
            this.renderer.removeClass(document.body, className);
        }
    }

    removeAllBackgroundClasses() {
        BACKGROUND_CLASS_NAME_ARRAY.forEach((className) => this.renderer.removeClass(document.body, className));
    }
}
