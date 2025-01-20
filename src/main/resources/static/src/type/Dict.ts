export class DictForm {
  dictOption: string;
  dictKey: string;


  constructor(dictOption: string, dictKey: string) {
    this.dictOption = dictOption;
    this.dictKey = dictKey;
  }

}

export class DictInfo {
  fieldName: string;
  fieldDesc: string;
  enumType: string;
  enumSql: string;
  pairSize: number;

  constructor(fieldName: string, fieldDesc: string, enumType: string, enumSql: string, pairSize: number) {
    this.fieldName = fieldName;
    this.fieldDesc = fieldDesc;
    this.enumType = enumType;
    this.enumSql = enumSql;
    this.pairSize = pairSize;
  }

}

export class Pair {
  key: string;
  value: string;

  constructor(key: string, value: string) {
    this.key = key;
    this.value = value;
  }

  invalid(): boolean {
    return this.key == undefined || this.key == null || this.key == ''
        || this.value == undefined || this.value == null || this.value == '';
  }
}

export class DictEdit {
  fieldName: string;
  fieldDesc: string;
  enumType: string;
  enumSql: string;
  pairs: Pair[];

  constructor(fieldName: string, fieldDesc: string, enumType: string, enumSql: string, pairs: Pair[]) {
    this.fieldName = fieldName;
    this.fieldDesc = fieldDesc;
    this.enumType = enumType;
    this.enumSql = enumSql;
    this.pairs = pairs;
  }

  copyWithRouteQuery(query: any): void {
    this.fieldName = query.fieldName
    this.fieldDesc = query.fieldDesc
    this.enumType = query.enumType
    this.enumSql = query.enumSql
    this.pairs = []
  }

  isSqlEnum(): boolean {
    return "sql" == this.enumType
  }

  isHandEnum(): boolean {
    return "enum" == this.enumType
  }

}


