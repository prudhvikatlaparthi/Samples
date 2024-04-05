import React from 'react'
import { Text } from 'react-native'
import FONT from '../theme/theme';

export default class AppText extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    const data = {
      fontFamily: this.props.fontType == 'bold' ? FONT.bold : this.props.fontType == 'medium' ? FONT.medium : FONT.regular
    }
    return (
      <Text style={[data, this.props.style]}>
        {this.props.children}
      </Text>
    );
  }
}