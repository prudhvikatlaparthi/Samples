import { StyleSheet, Dimensions } from "react-native";
import Colors from "./Colors.style";

const styles = StyleSheet.create({
  container: {
    backgroundColor: Colors.colorPrimary,
    flex: 1,
    marginHorizontal: 10
  },
  inputTextStyle: {
    fontSize: 20,
  },
  buttonStyle: {
    fontSize: 15,
    color: Colors.colorAccent,
    fontWeight: '800'
  },
  noteItemStyle: {
    width: ((Dimensions.get('window').width) - 50) / 2,
    marginTop: 10,
    marginStart:10,
    padding: 10,
    borderRadius: 10,
  }
});

export default styles;
