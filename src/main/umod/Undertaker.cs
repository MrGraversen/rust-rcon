using Newtonsoft.Json;
using Oxide.Core;
using Rust;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using UnityEngine;

namespace Oxide.Plugins
{
    [Info("Undertaker (Ownzone)", "Ownzone", "0.0.1")]
    public class Undertaker : RustPlugin
    {
        void OnRawDeathNotice(Dictionary<string, string> data, string message)
        {
            Puts(JsonConvert.SerializeObject(data));
        }
    }
}