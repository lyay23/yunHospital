import { isArray } from "../../../util";
import { processQuery } from "../../_predicates";
const processBitwiseQuery = (selector, value, predicate) => {
  return processQuery(
    selector,
    value,
    null,
    (value2, mask) => {
      let b = 0;
      if (isArray(mask)) {
        for (const n of mask) b = b | 1 << n;
      } else {
        b = mask;
      }
      return predicate(value2 & b, b);
    }
  );
};
export {
  processBitwiseQuery
};
