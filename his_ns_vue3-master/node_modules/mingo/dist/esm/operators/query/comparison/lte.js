import { $lte as __lte, processQuery } from "../../_predicates";
const $lte = (selector, value, options) => processQuery(selector, value, options, __lte);
export {
  $lte
};
