import {
    ChangeDetectorRef,
    Component,
    ElementRef,
    forwardRef,
    HostListener,
    Input,
    ViewChild
} from '@angular/core';
import { Observable } from 'rxjs/Rx';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TypeaheadMultiselectModel } from '../typeahead-multiselect-model';
import { TypeaheadItemDisplayModel } from '../typeahead-item-display-model';
import {
    MULTISELECT_FREE_TEXT_VALUE_MIN_LENGTH,
    TYPEAHEAD_QUERY_MIN_LENGTH
} from '../../../../app.constants';
import { NgbTooltip, NgbTypeahead } from '@ng-bootstrap/ng-bootstrap';

enum Key {
    Tab = 9,
    Enter = 13,
}

@Component({
    selector: 'jr2-typeahead-multiselect',
    templateUrl: './typeahead-multiselect.component.html',
    styleUrls: ['./typeahead-multiselect.component.scss'],
    providers: [{
        provide: NG_VALUE_ACCESSOR,
        useExisting: forwardRef(() => TypeaheadMultiselectComponent),
        multi: true
    }]
})
export class TypeaheadMultiselectComponent implements ControlValueAccessor {
    @Input() itemLoader: (text: string) => Observable<TypeaheadMultiselectModel[]>;
    @Input() placeholder: string;
    @Input() editable = true;
    @Input() focusFirst = false;
    @Input() tooltip: string;
    @Input() limit = 0;
    @Input() size: 'sm' | 'lg' = 'sm';

    @ViewChild(NgbTypeahead) ngbTypeahead;
    @ViewChild('t') ngbTooltip: NgbTooltip;

    inputValue: string;
    selectedItems: Array<TypeaheadMultiselectModel> = [];

    constructor(private changeDetectorRef: ChangeDetectorRef,
                private elRef: ElementRef) {
    }

    registerOnChange(fn: (value: any) => any): void {
        this._onChange = fn;
    }

    registerOnTouched(fn: () => any): void {
        this._onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
    }

    writeValue(obj: any): void {
        if (obj) {
            this.selectedItems = [...obj];
            this.changeDetectorRef.markForCheck();
        }
    }

    formatResultItem(item: TypeaheadMultiselectModel) {
        return item.label;
    }

    getItemClass(item: TypeaheadMultiselectModel) {
        return `typeahead-multiselect__tag--${item.type}`;
    }

    getTypeClass(item: TypeaheadMultiselectModel) {
        return `typeahead-multiselect__type-label--${item.type}`;
    }

    removeItem(item: TypeaheadMultiselectModel) {
        const filteredItems = this.selectedItems.filter((i: TypeaheadMultiselectModel) => !item.equals(i));

        this._onChange(filteredItems);
        this.writeValue(filteredItems);

        this.getTypeaheadNativeElement().focus();
    }

    focusInput() {
        this.getTypeaheadNativeElement().focus();
    }

    handleKeyDown(event: KeyboardEvent) {
        if (event.which === Key.Enter || event.which === Key.Tab) {
            if (this.selectFreeText()) {
                event.preventDefault();
                event.stopPropagation();
            }
        } else if (!this.canSelect()) {
            event.preventDefault();
        }
    }

    getInputWidth() {
        const value = this.getTypeaheadNativeElement().value || '';
        if (value.length > 0) {
            return `${value.length}em`;
        } else if (this.selectedItems.length > 0) {
            return '0.5em';
        } else {
            return '100%';
        }
    }

    private getTypeaheadNativeElement() {
        return this.ngbTypeahead._elementRef.nativeElement;
    }

    selectFreeText() {
        const freeText = new TypeaheadMultiselectModel('free-text', this.inputValue, this.inputValue);
        if (this.canSelect() && this.editable && !this.exists(freeText) && freeText.code
            && freeText.code.length >= MULTISELECT_FREE_TEXT_VALUE_MIN_LENGTH) {

            const newItems = [...this.selectedItems, freeText];

            this._onChange(newItems);
            this.writeValue(newItems);

            this.clearInput();
            return freeText;
        }
        return null;
    }

    private canSelect(): boolean {
        return !this.limit || this.selectedItems.length < this.limit;
    }

    selectItem(event: any) {
        event.preventDefault();

        if (!this.canSelect()) {
           return;
        }

        const newItems = [...this.selectedItems, event.item.model];

        this._onChange(newItems);
        this.writeValue(newItems);

        this.clearInput();
    }

    showPlaceholder(): boolean {
        return this.selectedItems.length === 0;
    }

    wrappedItemLoader = (text$: Observable<string>): Observable<TypeaheadItemDisplayModel[]> => {

        const toDisplayModel = (m: TypeaheadMultiselectModel, idx: number, array: TypeaheadMultiselectModel[]) => {
            let fistInGroup = false;
            if (idx === 0 || m.type !== array[idx - 1].type) {
                fistInGroup = true;
            }
            return new TypeaheadItemDisplayModel(m, idx === 0, fistInGroup);
        };

        const toDisplayModelArray = (items: TypeaheadMultiselectModel[]) => items
            .filter((m: TypeaheadMultiselectModel) => !this.exists(m))
            .sort((o1: TypeaheadMultiselectModel, o2: TypeaheadMultiselectModel) => o1.compare(o2))
            .map(toDisplayModel);

        return text$
            .switchMap((query: string) => query.length >= TYPEAHEAD_QUERY_MIN_LENGTH
                ? this.itemLoader(query)
                : Observable.of([]))
            .map(toDisplayModelArray);
    };

    private exists(model: TypeaheadMultiselectModel) {
        return !!this.selectedItems.find((i: TypeaheadMultiselectModel) => model.equals(i));
    }

    private _onChange = (_: any) => {
    };

    private _onTouched = () => {
    };

    @HostListener('document:click', ['$event.target'])
    onClick(targetElement: HTMLElement): void {
        if (!targetElement) {
            return;
        }

        if (!this.elRef.nativeElement.contains(targetElement)) {
            if (!this.selectFreeText()) {
                this.clearInput();
            }

            this.closeTooltip();
        }
    }

    private clearInput(): void {
        // This hack removes the invalid value from the input field.
        // The idea is from this PR: https://github.com/ng-bootstrap/ng-bootstrap/pull/1468
        //
        // todo: We have to review this after updating to the next ng-bootstrap versions.
        this.ngbTypeahead._userInput = '';
        this.inputValue = '';
    }

    openTooltip(): void {
        if (this.tooltip) {
            this.ngbTooltip.open();
        }
    }

    closeTooltip(): void {
        if (this.tooltip) {
            this.ngbTooltip.close();
        }
    }
}
