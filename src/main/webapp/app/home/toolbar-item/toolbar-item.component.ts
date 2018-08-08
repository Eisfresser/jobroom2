import {
    Component,
    ElementRef,
    EventEmitter,
    HostListener,
    Input,
    OnInit,
    Output
} from '@angular/core';

@Component({
    selector: 'jr2-toolbar-item',
    templateUrl: './toolbar-item.component.html',
    styleUrls: ['./toolbar-item.component.scss']
})
export class ToolbarItemComponent implements OnInit {
    @Input() icon: string;
    @Input() active: boolean;
    @Output() select = new EventEmitter();

    private readonly ENTER_KEY_CODE = 13;

    constructor(private elRef: ElementRef) {
    }

    ngOnInit() {
    }

    handleClick() {
        this.select.emit();
    }

    isNavLink() {
        return !!this.icon;
    }

    @HostListener('document:keypress', ['$event.target', '$event'])
    handleKeyboardEvent(targetElement: HTMLElement, event: KeyboardEvent) {
        if (!targetElement) {
            return;
        }

        if (event.keyCode === this.ENTER_KEY_CODE
            && this.elRef.nativeElement.contains(targetElement)) {
            event.preventDefault();
            event.stopPropagation();
            this.elRef.nativeElement.querySelector('a.nav-link').click();
        }
    }
}
