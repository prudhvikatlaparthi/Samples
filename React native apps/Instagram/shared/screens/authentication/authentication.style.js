import { StyleSheet } from "react-native";
import FONT from "../../theme/theme";

const styles = StyleSheet.create({
  parent: {
    marginVertical: 10
  },
  container: {
    margin: 20,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 1,
    borderColor: '#DBDBDB',
    borderWidth: 0.2,
  },
  largeText: {
    fontSize: 30,
  },
  inputTextStyle: {
    borderWidth: 0.5,
    borderColor: '#DBDBDB',
    backgroundColor: "#FAFAFA",
    borderRadius: 3,
    padding: 8,
    width: "80%",
    marginTop: 10,
    fontFamily: FONT.regular
  },
});

export default styles;
