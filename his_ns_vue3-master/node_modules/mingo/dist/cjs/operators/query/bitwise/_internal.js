var __defProp = Object.defineProperty;
var __getOwnPropDesc = Object.getOwnPropertyDescriptor;
var __getOwnPropNames = Object.getOwnPropertyNames;
var __hasOwnProp = Object.prototype.hasOwnProperty;
var __export = (target, all) => {
  for (var name in all)
    __defProp(target, name, { get: all[name], enumerable: true });
};
var __copyProps = (to, from, except, desc) => {
  if (from && typeof from === "object" || typeof from === "function") {
    for (let key of __getOwnPropNames(from))
      if (!__hasOwnProp.call(to, key) && key !== except)
        __defProp(to, key, { get: () => from[key], enumerable: !(desc = __getOwnPropDesc(from, key)) || desc.enumerable });
  }
  return to;
};
var __toCommonJS = (mod) => __copyProps(__defProp({}, "__esModule", { value: true }), mod);
var internal_exports = {};
__export(internal_exports, {
  processBitwiseQuery: () => processBitwiseQuery
});
module.exports = __toCommonJS(internal_exports);
var import_util = require("../../../util");
var import_predicates = require("../../_predicates");
const processBitwiseQuery = (selector, value, predicate) => {
  return (0, import_predicates.processQuery)(
    selector,
    value,
    null,
    (value2, mask) => {
      let b = 0;
      if ((0, import_util.isArray)(mask)) {
        for (const n of mask) b = b | 1 << n;
      } else {
        b = mask;
      }
      return predicate(value2 & b, b);
    }
  );
};
// Annotate the CommonJS export names for ESM import in node:
0 && (module.exports = {
  processBitwiseQuery
});
