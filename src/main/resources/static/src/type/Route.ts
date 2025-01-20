import {jsonPretty} from "@/utils/Utils";

export class RouteTemplate {
  tid: number;
  uri: string;
  predicates: string;
  filters: string;
  order: number;

  constructor(tid: number, uri: string, predicates: string, filters: string, order: number) {
    this.tid = tid;
    this.uri = uri;
    this.predicates = predicates;
    this.filters = filters;
    this.order = order;
  }

  copy(t: RouteTemplate): void {
    this.tid = t.tid;
    this.uri = t.uri;
    this.predicates = t.predicates;
    this.filters = t.filters;
    this.order = t.order;
  }
}

export class RouteInfo {
  rid: string;
  uri: string;
  predicates: string;
  filters: string;
  order: number;

  constructor(rid: string, uri: string, predicates: string, filters: string, order: number) {
    this.rid = rid;
    this.uri = uri;
    this.predicates = predicates;
    this.filters = filters;
    this.order = order;
  }

  setUseTemp(t: RouteTemplate): void {
    this.uri = t.uri;
    this.predicates = jsonPretty(t.predicates);
    this.filters = jsonPretty(t.filters);
    this.order = t.order;
  }

  toRestfulObj(): any {
    return {
      id: this.rid, uri: this.uri,
      predicates: JSON.parse(this.predicates),
      filters: JSON.parse(this.filters), order: this.order
    }
  }

  copy(obj: any): void {
    this.rid = obj.id;
    this.uri = obj.uri;
    this.predicates = jsonPretty(obj.predicates);
    this.filters = jsonPretty(obj.filters);
    this.order = obj.order;
  }
}
