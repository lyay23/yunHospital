import { $eq as __eq, processQuery } from "../../_predicates";
const $eq = (selector, value, options) => processQuery(selector, value, options, __eq);
export {
  $eq
};
