import { $eq as __eq, processExpression } from "../../_predicates";
const $eq = (obj, expr, options) => processExpression(obj, expr, options, __eq);
export {
  $eq
};
