import { processOperator } from "./_internal";
const $cosh = (obj, expr, options) => processOperator(obj, expr, options, Math.cosh, {
  "-Infinity": Infinity,
  Infinity: Infinity
});
export {
  $cosh
};
