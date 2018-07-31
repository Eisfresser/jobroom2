import { NgbDateStruct, NgbTimeStruct } from '@ng-bootstrap/ng-bootstrap';
import * as moment from 'moment';

export class DateUtils {

    static mapDateToNgbDateStruct(source?: Date): NgbDateStruct {
        const date = source ? source : new Date();
        return {
            year: date.getFullYear(),
            month: date.getMonth() + 1,
            day: date.getDate()
        };
    }

    static mapNgbDateStructToDate(dateStruct: NgbDateStruct): Date {
        return new Date(dateStruct.year, dateStruct.month - 1, dateStruct.day);
    }

    static dateStringToNgbDateStruct(date: string): NgbDateStruct {
        if (!date) {
            return null;
        }

        const { year, month, day } = DateUtils.dateStringToNgbDateTimeStruct(date);
        return { year, month, day };
    }

    static dateStringToNgbDateTimeStruct(date: string): NgbDateStruct & NgbTimeStruct {
        if (!date) {
            return null;
        }

        const parsedDate = moment(date);

        if (!parsedDate.isValid()) {
            throw 'Wrong date format';
        }

        return {
            year: parsedDate.year(),
            month: parsedDate.month() + 1,
            day: parsedDate.date(),
            hour: parsedDate.hour(),
            minute: parsedDate.minute(),
            second: parsedDate.second()
        };
    }

    static convertNgbDateStructToString(date: NgbDateStruct): string {
        return date ? moment(DateUtils.mapNgbDateStructToDate(date)).format('YYYY-MM-DD') : null;
    }

    static convertNgbDateTimeToISOWithoutZone(date: NgbDateStruct, time: NgbTimeStruct): string {
        return moment().date(date.day).month(date.month - 1).year(date.year)
            .hour(time.hour).minute(time.minute).second(0).millisecond(0)
            .format('YYYY-MM-DDTHH:mm:ss.sss');
    }
}
