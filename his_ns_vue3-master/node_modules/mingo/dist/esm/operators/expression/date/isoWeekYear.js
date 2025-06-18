import { computeDate, isoWeekYear } from "./_internal";
const $isoWeekYear = (obj, expr, options) => isoWeekYear(computeDate(obj, expr, options));
export {
  $isoWeekYear
};
