import { processOperator } from "./_internal";
const $acos = (obj, expr, options) => processOperator(obj, expr, options, Math.acos, {
  Infinity: Infinity,
  0: new Error()
});
export {
  $acos
};
