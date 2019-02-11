// These constants are injected via webpack environment variables.
// You can add more variables in webpack.common.js or in profile specific webpack.<dev|prod>.js files.
// If you change the values in the webpack config files, you need to re run webpack to update the application

export const VERSION = process.env.VERSION;
export const DEBUG_INFO_ENABLED: boolean = !!process.env.DEBUG_INFO_ENABLED;
export const USER_TRACKING_ENABLED = true;
export const SERVER_API_URL = process.env.SERVER_API_URL || '/';
export const BUILD_TIMESTAMP = process.env.BUILD_TIMESTAMP;
export const TYPEAHEAD_QUERY_MIN_LENGTH = 2;
export const MULTISELECT_FREE_TEXT_VALUE_MIN_LENGTH = 2;
export const DATE_FORMAT = 'DD.MM.YYYY';
export const TOOLTIP_AUTO_HIDE_TIMEOUT = 2500;
