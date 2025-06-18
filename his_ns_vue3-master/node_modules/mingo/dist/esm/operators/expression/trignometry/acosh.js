import { processOperator } from "./_internal";
const $acosh = (obj, expr, options) => processOperator(obj, expr, options, Math.acosh, {
  Infinity: Infinity,
  0: new Error()
});
export {
  $acosh
};
